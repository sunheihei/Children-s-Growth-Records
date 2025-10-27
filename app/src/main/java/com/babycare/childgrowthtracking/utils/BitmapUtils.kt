package com.babycare.childgrowthtracking.utils

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.util.Log
import com.babycare.childgrowthtracking.AppContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.UUID
import kotlin.io.encoding.ExperimentalEncodingApi

object BitmapUtils {

    // 从drawable资源获取压缩后的Base64字符串
    fun drawableToCompressedBase64(context: Context, drawableResId: Int): String? {
        return try {
            // 解码选项
            val options = BitmapFactory.Options().apply {
                // 第一步：只解码边界信息
                inJustDecodeBounds = true
                BitmapFactory.decodeResource(context.resources, drawableResId, this)

                // 计算采样率，目标宽度设为800px（可调整）
                val maxWidth = 500
                inSampleSize = calculateInSampleSize(this, maxWidth)
                inJustDecodeBounds = false
            }

            // 第二步：使用采样率加载压缩后的图片
            val bitmap = BitmapFactory.decodeResource(context.resources, drawableResId, options)

            // 转换为Base64
            val base64String = bitmapToBase64(bitmap)
            bitmap.recycle() // 释放Bitmap内存
            base64String
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /// 将URI转换为压缩后的Base64字符串
    suspend fun uriToCompressedBase64(context: Context, uri: Uri): String? {
        return withContext(Dispatchers.IO) {
            try {
                // 获取输入流
                val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
                val options = BitmapFactory.Options().apply {
                    // 先只解码边界信息，不加载整个图片到内存
                    inJustDecodeBounds = true
                    BitmapFactory.decodeStream(inputStream, null, this)
                    inputStream?.close()

                    // 计算采样率，目标宽度设为800px（可调整）
                    val maxWidth = 500
                    inSampleSize = calculateInSampleSize(this, maxWidth)
                    inJustDecodeBounds = false
                }

                // 第二次读取，使用采样率加载压缩后的图片
                val secondInputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(secondInputStream, null, options)
                secondInputStream?.close()

                // 处理图片旋转
                bitmap?.let {
                    // 获取 EXIF 方向信息
                    val exifStream = context.contentResolver.openInputStream(uri)
                    val exif = exifStream?.let { ExifInterface(it) }
                    val orientation = exif?.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                    ) ?: ExifInterface.ORIENTATION_NORMAL
                    exifStream?.close()

                    // 根据方向旋转 Bitmap
                    val rotatedBitmap = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(it, 90f)
                        ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(it, 180f)
                        ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(it, 270f)
                        else -> it // 无需旋转
                    }

                    // 转换为 Base64
                    val base64String = bitmapToBase64(rotatedBitmap)

                    // 释放内存
                    if (rotatedBitmap != it) rotatedBitmap.recycle()
                    it.recycle()

                    base64String
                }
            } catch (e: Exception) {
                Log.e("PhotoPicker", "Error compressing image: ${e.message}")
                null
            }
        }
    }


    /// 旋转 Bitmap
    private fun rotateBitmap(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix().apply { postRotate(degrees) }
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    // 计算采样率
    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int): Int {
        val width = options.outWidth
        var inSampleSize = 1

        if (width > reqWidth) {
            val halfWidth = width / 2
            while (halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    // 将Bitmap图片转换为Base64字符串
    @OptIn(ExperimentalEncodingApi::class)
    fun bitmapToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        // 压缩图片到输出流中，100表示质量(0-100)，PNG是无损格式不会受质量影响
        bitmap.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        // 将字节数组转换为Base64字符串
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    // 将Base64字符串转换为Bitmap图片
    fun base64ToBitmap(base64String: String): Bitmap? {
        return try {
            // 将Base64字符串解码为字节数组
            val decodedByteArray = Base64.decode(base64String, Base64.DEFAULT)
            // 将字节数组转换为Bitmap
            BitmapFactory.decodeByteArray(decodedByteArray, 0, decodedByteArray.size)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun copyAndCompressPhoto(uri: Uri): String {
        // 加载 Bitmap
        val bitmap =
            AppContext.getContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            } ?: throw IllegalStateException("Failed to load bitmap from $uri")
        // 调整角度
        val adjustedBitmap = adjustBitmapOrientation(uri, bitmap)
        // 可选：缩放图片（限制最大尺寸为 1024px）
        val maxSize = 1024
        val scaledBitmap = if (adjustedBitmap.width > maxSize || adjustedBitmap.height > maxSize) {
            val ratio = minOf(
                maxSize.toFloat() / adjustedBitmap.width, maxSize.toFloat() / adjustedBitmap.height
            )
            val newWidth = (adjustedBitmap.width * ratio).toInt()
            val newHeight = (adjustedBitmap.height * ratio).toInt()
            Bitmap.createScaledBitmap(adjustedBitmap, newWidth, newHeight, true)
        } else {
            adjustedBitmap
        }
        // 保存压缩后的图片
        val fileName = "${UUID.randomUUID()}.jpg"
        val file = File(AppContext.getContext().filesDir, fileName)
        FileOutputStream(file).use { outputStream ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        }
        return file.absolutePath
    }

    private fun adjustBitmapOrientation(uri: Uri, bitmap: Bitmap): Bitmap {
        try {
            AppContext.getContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                val exif = ExifInterface(inputStream)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL
                )
                val rotationAngle = when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> 90f
                    ExifInterface.ORIENTATION_ROTATE_180 -> 180f
                    ExifInterface.ORIENTATION_ROTATE_270 -> 270f
                    else -> 0f
                }
                if (rotationAngle != 0f) {
                    val matrix = Matrix().apply { postRotate(rotationAngle) }
                    return Bitmap.createBitmap(
                        bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                    )
                }
            }
        } catch (e: Exception) {
            Log.e("InsertDiary", "Failed to adjust orientation for $uri: ${e.message}")
        }
        return bitmap
    }

}
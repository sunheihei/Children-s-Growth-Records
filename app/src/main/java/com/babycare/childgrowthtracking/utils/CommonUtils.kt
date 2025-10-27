package com.babycare.childgrowthtracking.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import androidx.core.content.FileProvider
import com.babycare.childgrowthtracking.AppContext
import com.babycare.childgrowthtracking.model.GrowthRecord
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object CommonUtils {
    fun calculateAgeDifference(startTimestamp: Long, endTimestamp: Long): String {
        // 确保结束时间大于开始时间
        if (endTimestamp < startTimestamp) {
            return calculateAgeDifference(endTimestamp, startTimestamp)
        }

        // 计算时间差（毫秒）
        val diffMillis = endTimestamp - startTimestamp

        // 转换为天数
        val days = TimeUnit.MILLISECONDS.toDays(diffMillis)

        return when {
            // 小于1个月（假设30天为1个月）
            days < 30 -> {
                "$days days"
            }
            // 小于1年（365天）
            days < 365 -> {
                val months = days / 30
                val remainingDays = days % 30
                if (remainingDays == 0L) {
                    "$months months"
                } else {
                    "$months months $remainingDays days"
                }
            }
            // 大于等于1年
            else -> {
                val years = days / 365
                val remainingDaysAfterYears = days % 365
                val months = remainingDaysAfterYears / 30
                if (months == 0L) {
                    "$years years"
                } else {
                    "$years years $months months"
                }
            }
        }
    }


    // 生成 PDF 并分享的函数
    fun generateAndSharePdf(name: String, context: Context, growthRecords: List<GrowthRecord>) {
        try {
            // 创建 PDF 文件路径
            val fileName = "GrowthRecords_${name}.pdf"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

            // 初始化 PDF 文档
            val pdfWriter = PdfWriter(file)
            val pdfDocument = PdfDocument(pdfWriter)
            val document = Document(pdfDocument)
            document.add(Paragraph("$name Growth Data"));
            // 创建表格（列数根据 GrowthRecord 的字段定义）
            val columnCount =
                9 // date, heightCm, heightFt, heightIn, weightKg, weightLb, headCircleCm, headCircleIn, note
            val table = Table(UnitValue.createPercentArray(columnCount)).useAllAvailableWidth()
            // 添加表头
            table.addHeaderCell("Date")
            table.addHeaderCell("Height (cm)")
            table.addHeaderCell("Height (ft)")
            table.addHeaderCell("Height (in)")
            table.addHeaderCell("Weight (kg)")
            table.addHeaderCell("Weight (lb)")
            table.addHeaderCell("Head (cm)")
            table.addHeaderCell("Head (in)")
            table.addHeaderCell("Note")

            // 格式化日期
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

            // 添加数据行
            growthRecords.forEach { record ->
                table.addCell(dateFormat.format(Date(record.date)))
                table.addCell(record.heightCm)
                table.addCell(record.heightFt)
                table.addCell(record.heightIn)
                table.addCell(record.weightKg)
                table.addCell(record.weightLb)
                table.addCell(record.headCircleCm)
                table.addCell(record.headCircleIn)
                table.addCell(record.note)
            }

            // 将表格添加到文档
            document.add(table)
            document.close()

            // 分享 PDF 文件
            sharePdf(context, file)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // 分享 PDF 文件
    private fun sharePdf(context: Context, file: File) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider", // 需要配置 FileProvider
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share PDF"))
    }


    suspend fun getAppVersionName(): String = withContext(Dispatchers.IO) {
        AppContext.getContext().packageManager.getPackageInfo(
            AppContext.getContext().packageName,
            0
        ).versionName ?: "1.0.0"
    }


}
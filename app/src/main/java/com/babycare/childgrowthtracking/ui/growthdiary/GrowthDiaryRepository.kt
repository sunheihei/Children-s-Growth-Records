package com.babycare.childgrowthtracking.ui.growthdiary

import android.net.Uri
import android.util.Log
import com.babycare.childgrowthtracking.db.GrowthDiaryDao
import com.babycare.childgrowthtracking.model.DiaryPhoto
import com.babycare.childgrowthtracking.model.DiaryWithPhotos
import com.babycare.childgrowthtracking.model.GrowthDiary
import com.babycare.childgrowthtracking.utils.BitmapUtils.copyAndCompressPhoto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrowthDiaryRepository @Inject constructor(private val growthDiaryDao: GrowthDiaryDao) {

    // 根据 childId 获取日记及其图片
    fun getDiariesWithPhotosByChildId(childId: Int): Flow<List<DiaryWithPhotos>> {
        return growthDiaryDao.getDiariesWithPhotosByChildId(childId)
    }

    // 根据 ID 获取单篇日记及其图片
    fun getDiaryWithPhotosById(diaryId: Int): Flow<DiaryWithPhotos?> {
        return growthDiaryDao.getDiaryWithPhotosById(diaryId)
    }

    // 插入日记及其图片
    suspend fun insertDiaryWithPhotos(
        diary: GrowthDiary,
        photos: List<DiaryPhoto>
    ) = withContext(Dispatchers.IO) { // 使用 IO 线程
        val diaryId = growthDiaryDao.insertDiary(diary)
        // 串行处理图片
        val photosWithDiaryId = mutableListOf<DiaryPhoto>()
        photos.forEachIndexed { index, photo ->
            val uri = Uri.parse(photo.photoPath)
            val newPhotoPath = copyAndCompressPhoto(uri) // 假设此方法已定义
            val updatedPhoto = photo.copy(diaryId = diaryId.toInt(), photoPath = newPhotoPath)
            photosWithDiaryId.add(updatedPhoto)
        }
        growthDiaryDao.insertPhotos(photosWithDiaryId)
    }


    suspend fun deleteGrowthDiary(growthDiary: GrowthDiary) {
        growthDiaryDao.deleteGrowthDiary(growthDiary)
    }

    // 更新日记及其图片
    suspend fun updateDiaryWithPhotos(
        diary: GrowthDiary,
        photosToAdd: List<DiaryPhoto> = emptyList(),
        photoIdsToDelete: List<DiaryPhoto> = emptyList()
    ): Unit = withContext(Dispatchers.IO) { // 使用 IO 线程
        // 更新日记内容
        growthDiaryDao.updateDiary(diary)

        // 删除指定图片
        if (photoIdsToDelete.isNotEmpty()) {
            photoIdsToDelete.forEach { photo ->
                growthDiaryDao.deletePhotoById(photo.id)
                // 删除本地文件
                val file = File(photo.photoPath)
                if (file.exists()) {
                    val deleted = file.delete()
                    if (deleted) {
                        Log.d("DeletePhoto", "Deleted file: ${photo.photoPath}")
                    } else {
                        Log.w("DeletePhoto", "Failed to delete file: ${photo.photoPath}")
                    }
                } else {
                    Log.w("DeletePhoto", "File does not exist: ${photo.photoPath}")
                }
            }
        }

        // 添加新图片
        if (photosToAdd.isNotEmpty()) {
            // 串行处理图片
            val photosWithDiaryId = mutableListOf<DiaryPhoto>()
            photosToAdd.forEachIndexed { index, photo ->
                val uri = Uri.parse(photo.photoPath)
                val newPhotoPath = copyAndCompressPhoto(uri) // 假设此方法已定义
                val updatedPhoto = photo.copy(diaryId = diary.id, photoPath = newPhotoPath)
                photosWithDiaryId.add(updatedPhoto)
            }
            growthDiaryDao.insertPhotos(photosWithDiaryId)
        }
    }
}
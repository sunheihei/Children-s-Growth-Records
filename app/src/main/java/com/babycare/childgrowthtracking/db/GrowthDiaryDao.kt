package com.babycare.childgrowthtracking.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.babycare.childgrowthtracking.model.DiaryPhoto
import com.babycare.childgrowthtracking.model.DiaryWithPhotos
import com.babycare.childgrowthtracking.model.GrowthDiary
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthDiaryDao {

    // 插入日记
    @Insert
    suspend fun insertDiary(diary: GrowthDiary): Long

    // 插入图片
    @Insert
    suspend fun insertPhotos(photos: List<DiaryPhoto>): List<Long>

    // 根据 childId 查询日记及其图片
    @Transaction
    @Query("SELECT * FROM diary WHERE childId = :childId ORDER BY date DESC")
    fun getDiariesWithPhotosByChildId(childId: Int): Flow<List<DiaryWithPhotos>>

    // 根据 ID 查询单篇日记及其图片
    @Transaction
    @Query("SELECT * FROM diary WHERE id = :diaryId")
    fun getDiaryWithPhotosById(diaryId: Int): Flow<DiaryWithPhotos?>


    // 更新日记
    @Update
    suspend fun updateDiary(diary: GrowthDiary)

    // 删除指定图片
    @Query("DELETE FROM diary_photos WHERE id = :photoId")
    suspend fun deletePhotoById(photoId: Int)

    // 可选：根据 diaryId 删除所有图片
    @Query("DELETE FROM diary_photos WHERE diaryId = :diaryId")
    suspend fun deletePhotosByDiaryId(diaryId: Int)

    @Delete
    suspend fun deleteGrowthDiary(growthDiary: GrowthDiary)


}
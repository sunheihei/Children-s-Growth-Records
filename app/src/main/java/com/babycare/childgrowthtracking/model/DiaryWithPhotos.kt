package com.babycare.childgrowthtracking.model

import androidx.room.Embedded
import androidx.room.Relation
import com.babycare.childgrowthtracking.extensions.birthdayFormat
import java.util.Date

data class DiaryWithPhotos(
    @Embedded val diary: GrowthDiary,                          // 日记对象
    @Relation(
        parentColumn = "id",                                   // diary 表的主键
        entityColumn = "diaryId"                               // diary_photos 表的外键
    )
    val photos: List<DiaryPhoto>                               // 关联的图片列表
){
    companion object{
        val TempDiaryWithPhotos = DiaryWithPhotos(GrowthDiary(childId = 1, date = Date().time, content = "Growth Diary Content", title = "Dairy title"), emptyList())
    }
}
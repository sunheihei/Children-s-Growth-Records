package com.babycare.childgrowthtracking.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "diary_photos",
    foreignKeys = [
        ForeignKey(
            entity = GrowthDiary::class,          // 引用主表 GrowthDiary
            parentColumns = ["id"],               // 主表的主键列
            childColumns = ["diaryId"],           // 从表的外键列
            onDelete = ForeignKey.CASCADE,        // 日记删除时，相关图片记录也删除
            onUpdate = ForeignKey.CASCADE         // 日记 ID 更新时，同步更新
        )
    ],
    indices = [Index(value = ["diaryId"])]         // 为外键添加索引，提升查询效率
)
@Serializable
data class DiaryPhoto(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "diaryId") val diaryId: Int,  // 外键关联 diary 表
    @ColumnInfo(name = "photoPath") val photoPath: String  // 图片路径
)
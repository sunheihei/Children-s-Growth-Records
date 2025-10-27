package com.babycare.childgrowthtracking.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "diary", foreignKeys = [ForeignKey(
        entity = Child::class,
        parentColumns = ["id"],
        childColumns = ["childId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )], indices = [Index(value = ["childId"])]
)
@Serializable
data class GrowthDiary(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "childId") val childId: Int,  // 外键关联 Child 表
    var date: Long,                                  // 日记日期（时间戳）
    var title: String,                               // 日记标题
    var content: String                              // 日记内容
)

package com.babycare.childgrowthtracking.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Entity(
    tableName = "growth", foreignKeys = [ForeignKey(
        entity = Child::class,           // 引用主表类
        parentColumns = ["id"],          // 主表的主键列
        childColumns = ["childId"],      // 从表的外键列
        onDelete = ForeignKey.CASCADE,   // 主表记录删除时，从表相关记录也删除
        onUpdate = ForeignKey.CASCADE    // 主表主键更新时，从表同步更新
    )], indices = [Index(value = ["childId"])]
)
@Serializable
data class GrowthRecord(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
    @ColumnInfo(name = "childId") val childId: Int,  // 外键列
    var date: Long,
    var heightCm: String,
    var heightFt: String,
    var heightIn: String,
    var weightKg: String,
    var weightLb: String,
    var headCircleCm: String,
    var headCircleIn: String,
    var note: String
) {
    companion object {
        val TempGrowthRecord =
            GrowthRecord(0, 1, 1613034198L, "50.0", "3", "5", "6.1", "13.0", "20", "45", "note")

        val TempGrowthRecordList = listOf(TempGrowthRecord.copy(), TempGrowthRecord, TempGrowthRecord)
    }
}
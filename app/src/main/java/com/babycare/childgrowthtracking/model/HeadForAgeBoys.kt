package com.babycare.childgrowthtracking.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "head_for_age_boys")
data class HeadForAgeBoys(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    override val organize: Int,
    override val ageMonths: Int,
    override val p5: String,
    override val p25: String,
    override val p50: String,
    override val p75: String,
    override val p95: String,
) : GrowthStandard
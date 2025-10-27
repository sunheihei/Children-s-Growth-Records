package com.babycare.childgrowthtracking.utils

import kotlin.math.ceil
import kotlin.math.floor


data class MonthRange(val minMonth: Int, val maxMonth: Int)

object ChartsUtils {

    // 一个月的时间戳（毫秒）
    const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
    const val DAYS_PER_MONTH = 30.42 // 平均每月天数 (365 / 12)
    const val MILLIS_PER_MONTH = (DAYS_PER_MONTH * MILLIS_PER_DAY).toLong() // 约 2,628,288,000 毫秒

    fun calculateMonthRange(
        growthRecords: Map<Long, String>,
        birthTime: Long
    ): MonthRange {
        if (growthRecords.isEmpty()) {
            return MonthRange(0, 12) // 默认范围，例如 0-12 个月
        }

        // 找到最小和最大时间戳
        val minTimestamp = growthRecords.keys.minOrNull() ?: birthTime
        val maxTimestamp = growthRecords.keys.maxOrNull() ?: birthTime

        // 计算最小和最大月龄
        val minMonth = floor((minTimestamp - birthTime).toDouble() / MILLIS_PER_MONTH).toInt()
        val maxMonth = ceil((maxTimestamp - birthTime).toDouble() / MILLIS_PER_MONTH).toInt()

        // 确保最小月龄不小于 0
        val adjustedMinMonth = maxOf(0, minMonth)
        // 稍微扩展最大月龄，例如加 1 个月
        val adjustedMaxMonth = maxMonth + 1

        return MonthRange(adjustedMinMonth, adjustedMaxMonth)
    }

}
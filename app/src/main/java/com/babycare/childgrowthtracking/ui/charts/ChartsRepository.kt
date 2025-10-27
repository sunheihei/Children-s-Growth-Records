package com.babycare.childgrowthtracking.ui.charts

import com.babycare.childgrowthtracking.db.ChildrenDao
import com.babycare.childgrowthtracking.db.GrowthRecordDao
import com.babycare.childgrowthtracking.db.GrowthStandardDao
import com.babycare.childgrowthtracking.model.GrowthStandard
import com.babycare.childgrowthtracking.model.HeightForAgeGirls
import com.babycare.childgrowthtracking.model.Organize
import com.babycare.childgrowthtracking.utils.GENDER_BOY
import com.babycare.childgrowthtracking.utils.HEIGHT
import com.babycare.childgrowthtracking.utils.WEIGHT
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToInt

// 一个月平均时间戳（毫秒）
const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000L
const val DAYS_PER_MONTH = 30.42 // 平均每月天数 (365 / 12)
const val MILLIS_PER_MONTH = (DAYS_PER_MONTH * MILLIS_PER_DAY).toLong()

@Singleton
class ChartsRepository @Inject constructor(
    private val childrenDao: ChildrenDao,
    private val growthRecordDao: GrowthRecordDao,
    private val growthStandardDao: GrowthStandardDao
) {
    fun getChildAndGrowthRecords(childId: Int): Flow<ChartUiState> {
        return combine(
            childrenDao.getChildById(childId), // Flow<Child?>
            growthRecordDao.getByChildId(childId)
        ) { child, growthRecords ->
            if (child == null) {
                ChartUiState.Error("Child with ID $childId not found")
            } else {
                ChartUiState.Success(child, growthRecords)
            }
        }.catch { e ->
            emit(ChartUiState.Error("Failed to fetch data: ${e.message}"))
        }.flowOn(Dispatchers.IO)
    }


    suspend fun getStandardValuesForRecords(
        gender: Int,
        type: Int,
        growthRecords: Map<Long, String>,
        birthTime: Long,
        organize: Int
    ): List<GrowthStandard> {
        val result = mutableListOf<GrowthStandard>()

        // 按时间戳排序（可选，确保顺序）
        val sortedRecords = growthRecords.entries.sortedBy { it.key }

        // 遍历每个记录
        for (entry in sortedRecords) {
            val timestamp = entry.key
            // 计算月龄
            val timeDiffMillis = timestamp - birthTime
            val months = (timeDiffMillis.toDouble() / MILLIS_PER_MONTH).toFloat()
            val monthAge = months.roundToInt() // 四舍五入到整数月龄

            // 查询标准值
            val standardValue = if (gender == GENDER_BOY) {
                when (type) {
                    HEIGHT -> {
                        growthStandardDao.getHeightForAgeBoysByMonth(monthAge, organize)
                    }

                    WEIGHT -> {
                        growthStandardDao.getWeightForAgeBoysByMonth(monthAge, organize)
                    }

                    else -> {
                        growthStandardDao.getHeadForAgeBoysByMonth(monthAge, organize)
                    }


                }

            } else {
                when (type) {
                    HEIGHT -> {
                        growthStandardDao.getHeightForAgeGirlsByMonth(monthAge, organize)
                    }

                    WEIGHT -> {
                        growthStandardDao.getWeightForAgeGirlsByMonth(monthAge, organize)
                    }

                    else -> {
                        growthStandardDao.getHeadForAgeGirlsByMonth(monthAge, organize)
                    }

                }

            }

            if (standardValue != null) {
                result.add(standardValue)
            } else {
                // 如果没有匹配的月龄，返回一个默认值
                result.add(
                    HeightForAgeGirls(
                        organize = 0,
                        ageMonths = monthAge,
                        p5 = "0",
                        p25 = "0",
                        p50 = "0",
                        p75 = "0",
                        p95 = "0"
                    )
                )
            }
        }

        return result
    }


}
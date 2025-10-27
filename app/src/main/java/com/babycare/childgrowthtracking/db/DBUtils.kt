package com.babycare.childgrowthtracking.db

import android.content.Context
import android.util.Log
import com.babycare.childgrowthtracking.AppContext
import com.babycare.childgrowthtracking.model.HeadForAgeBoys
import com.babycare.childgrowthtracking.model.HeadForAgeGirls
import com.babycare.childgrowthtracking.model.HeightForAgeBoys
import com.babycare.childgrowthtracking.model.HeightForAgeGirls
import com.babycare.childgrowthtracking.model.WeightForAgeBoys
import com.babycare.childgrowthtracking.model.WeightForAgeGirls
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader

object DBUtils {
    fun initialize(scope: CoroutineScope, dao: GrowthStandardDao) {
        scope.launch {
            val girlsHeightSample = dao.getHeightForAgeGirls(0)
            val boysHeightSample = dao.getHeightForAgeBoys(0)
            if (girlsHeightSample == null || boysHeightSample == null) {
                importHeightDataFromCsv(dao)
            }
            val girlsWeightSample = dao.getWeightForAgeGirls(0)
            val boysWeightSample = dao.getWeightForAgeBoys(0)
            if (girlsWeightSample == null || boysWeightSample == null) {
                importWeightDataFromCsv(dao)
            }
            val girlsHeadSample = dao.getHeadForAgeGirls(0)
            val boysHeadSample = dao.getHeadForAgeBoys(0)
            if (girlsHeadSample == null || boysHeadSample == null) {
                importHeadDataFromCsv(dao)
            }
        }
    }

    private suspend fun importHeightDataFromCsv(heightForAgeDao: GrowthStandardDao) {
        withContext(Dispatchers.IO) {
            try {
                // 导入女孩数据
                val girlsInputStream = AppContext.getContext().assets.open("girl_height.csv")
                val girlsReader = BufferedReader(InputStreamReader(girlsInputStream))
                val girlsData = mutableListOf<HeightForAgeGirls>()
                girlsReader.readLine() // 跳过表头
                var line: String?
                while (girlsReader.readLine().also { line = it } != null) {
                    val parts = line!!.split(",")
                    if (parts.size == 7) {
                        val organize = parts[0].toInt()
                        val ageMonths = parts[1].toInt()
                        val p5 = parts[2]
                        val p25 = parts[3]
                        val p50 = parts[4]
                        val p75 = parts[5]
                        val p95 = parts[6]
                        girlsData.add(
                            HeightForAgeGirls(
                                organize = organize,
                                ageMonths = ageMonths,
                                p5 = p5,
                                p25 = p25,
                                p50 = p50,
                                p75 = p75,
                                p95 = p95
                            )
                        )
                    }
                }
                heightForAgeDao.insertGirls(girlsData)
                Log.d("ImportCsv", "Successfully imported ${girlsData.size} girls records")

                // 导入男孩数据
                val boysInputStream = AppContext.getContext().assets.open("boy_height.csv")
                val boysReader = BufferedReader(InputStreamReader(boysInputStream))
                val boysData = mutableListOf<HeightForAgeBoys>()
                boysReader.readLine() // 跳过表头
                while (boysReader.readLine().also { line = it } != null) {
                    val parts = line!!.split(",")
                    if (parts.size == 7) {
                        val organize = parts[0].toInt()
                        val ageMonths = parts[1].toInt()
                        val p5 = parts[2]
                        val p25 = parts[3]
                        val p50 = parts[4]
                        val p75 = parts[5]
                        val p95 = parts[6]
                        boysData.add(
                            HeightForAgeBoys(
                                organize = organize,
                                ageMonths = ageMonths,
                                p5 = p5,
                                p25 = p25,
                                p50 = p50,
                                p75 = p75,
                                p95 = p95
                            )
                        )
                    }
                }
                heightForAgeDao.insertBoys(boysData)
                Log.d("ImportCsv", "Successfully imported ${boysData.size} boys records")
            } catch (e: Exception) {
                Log.e("ImportCsv", "Error importing CSV: ${e.message}")
            }
        }
    }

    private suspend fun importWeightDataFromCsv(dao: GrowthStandardDao) {
        withContext(Dispatchers.IO) {
            // 导入女孩体重数据
            val girlsInputStream = AppContext.getContext().assets.open("girl_weight.csv")
            val girlsReader = BufferedReader(InputStreamReader(girlsInputStream))
            val girlsData = mutableListOf<WeightForAgeGirls>()
            girlsReader.readLine() // 跳过表头
            var line: String?
            while (girlsReader.readLine().also { line = it } != null) {
                val parts = line!!.split(",")
                if (parts.size == 7) {
                    val organize = parts[0].toInt()
                    val ageMonths = parts[1].toInt()
                    val p5 = parts[2]
                    val p25 = parts[3]
                    val p50 = parts[4]
                    val p75 = parts[5]
                    val p95 = parts[6]
                    girlsData.add(
                        WeightForAgeGirls(
                            organize = organize,
                            ageMonths = ageMonths,
                            p5 = p5,
                            p25 = p25,
                            p50 = p50,
                            p75 = p75,
                            p95 = p95
                        )
                    )
                }
            }
            dao.insertWeightGirls(girlsData)
            Log.d("ImportCsv", "Successfully imported ${girlsData.size} weight girls records")

            // 导入男孩体重数据
            val boysInputStream = AppContext.getContext().assets.open("boy_weight.csv")
            val boysReader = BufferedReader(InputStreamReader(boysInputStream))
            val boysData = mutableListOf<WeightForAgeBoys>()
            boysReader.readLine() // 跳过表头
            while (boysReader.readLine().also { line = it } != null) {
                val parts = line!!.split(",")
                if (parts.size == 7) {
                    val organize = parts[0].toInt()
                    val ageMonths = parts[1].toInt()
                    val p5 = parts[2]
                    val p25 = parts[3]
                    val p50 = parts[4]
                    val p75 = parts[5]
                    val p95 = parts[6]
                    boysData.add(
                        WeightForAgeBoys(
                            organize = organize,
                            ageMonths = ageMonths,
                            p5 = p5,
                            p25 = p25,
                            p50 = p50,
                            p75 = p75,
                            p95 = p95
                        )
                    )
                }
            }
            dao.insertWeightBoys(boysData)
            Log.d("ImportCsv", "Successfully imported ${boysData.size} weight boys records")
        }
    }


    private suspend fun importHeadDataFromCsv(dao: GrowthStandardDao) {
        withContext(Dispatchers.IO) {
            // 导入女孩头围数据
            val girlsInputStream = AppContext.getContext().assets.open("girl_hcfa.csv")
            val girlsReader = BufferedReader(InputStreamReader(girlsInputStream))
            val girlsData = mutableListOf<HeadForAgeGirls>()
            girlsReader.readLine() // 跳过表头
            var line: String?
            while (girlsReader.readLine().also { line = it } != null) {
                val parts = line!!.split(",")
                if (parts.size == 7) {
                    val organize = parts[0].toInt()
                    val ageMonths = parts[1].toInt()
                    val p5 = parts[2]
                    val p25 = parts[3]
                    val p50 = parts[4]
                    val p75 = parts[5]
                    val p95 = parts[6]
                    girlsData.add(
                        HeadForAgeGirls(
                            organize = organize,
                            ageMonths = ageMonths,
                            p5 = p5,
                            p25 = p25,
                            p50 = p50,
                            p75 = p75,
                            p95 = p95
                        )
                    )
                }
            }
            dao.insertHeadGirls(girlsData)
            Log.d("ImportCsv", "Successfully imported ${girlsData.size} head girls records")

            // 导入男孩头围数据
            val boysInputStream = AppContext.getContext().assets.open("boy_hcfa.csv")
            val boysReader = BufferedReader(InputStreamReader(boysInputStream))
            val boysData = mutableListOf<HeadForAgeBoys>()
            boysReader.readLine() // 跳过表头
            while (boysReader.readLine().also { line = it } != null) {
                val parts = line!!.split(",")
                if (parts.size == 7) {
                    val organize = parts[0].toInt()
                    val ageMonths = parts[1].toInt()
                    val p5 = parts[2]
                    val p25 = parts[3]
                    val p50 = parts[4]
                    val p75 = parts[5]
                    val p95 = parts[6]
                    boysData.add(
                        HeadForAgeBoys(
                            organize = organize,
                            ageMonths = ageMonths,
                            p5 = p5,
                            p25 = p25,
                            p50 = p50,
                            p75 = p75,
                            p95 = p95
                        )
                    )
                }
            }
            dao.insertHeadBoys(boysData)
            Log.d("ImportCsv", "Successfully imported ${boysData.size} head boys records")
        }
    }

}
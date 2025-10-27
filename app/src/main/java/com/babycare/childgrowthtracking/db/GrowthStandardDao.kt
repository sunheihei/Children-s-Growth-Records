package com.babycare.childgrowthtracking.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.babycare.childgrowthtracking.model.HeadForAgeBoys
import com.babycare.childgrowthtracking.model.HeadForAgeGirls
import com.babycare.childgrowthtracking.model.HeightForAgeBoys
import com.babycare.childgrowthtracking.model.HeightForAgeGirls
import com.babycare.childgrowthtracking.model.Organize
import com.babycare.childgrowthtracking.model.WeightForAgeBoys
import com.babycare.childgrowthtracking.model.WeightForAgeGirls

@Dao
interface GrowthStandardDao {
    // 插入女孩数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGirls(data: List<HeightForAgeGirls>)

    // 插入男孩数据
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBoys(data: List<HeightForAgeBoys>)

    // 查询女孩数据
    @Query("SELECT * FROM height_for_age_girls WHERE ageMonths = :ageMonths")
    suspend fun getHeightForAgeGirls(ageMonths: Int): HeightForAgeGirls?


    @Query("SELECT * FROM height_for_age_girls WHERE (ageMonths = :ageMonths And organize=:organize) LIMIT 1")
    suspend fun getHeightForAgeGirlsByMonth(ageMonths: Int, organize: Int): HeightForAgeGirls?

    // 查询男孩数据
    @Query("SELECT * FROM height_for_age_boys WHERE ageMonths = :ageMonths")
    suspend fun getHeightForAgeBoys(ageMonths: Int): HeightForAgeBoys?


    @Query("SELECT * FROM height_for_age_boys WHERE (ageMonths = :ageMonths And organize=:organize) LIMIT 1")
    suspend fun getHeightForAgeBoysByMonth(ageMonths: Int, organize: Int): HeightForAgeBoys?

    // 体重数据操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightGirls(data: List<WeightForAgeGirls>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightBoys(data: List<WeightForAgeBoys>)

    @Query("SELECT * FROM weight_for_age_girls WHERE ageMonths = :ageMonths")
    suspend fun getWeightForAgeGirls(ageMonths: Int): WeightForAgeGirls?


    @Query("SELECT * FROM weight_for_age_girls WHERE (ageMonths = :ageMonths And organize=:organize) LIMIT 1")
    suspend fun getWeightForAgeGirlsByMonth(ageMonths: Int, organize: Int): WeightForAgeGirls?

    @Query("SELECT * FROM weight_for_age_boys WHERE ageMonths = :ageMonths")
    suspend fun getWeightForAgeBoys(ageMonths: Int): WeightForAgeBoys?


    @Query("SELECT * FROM weight_for_age_boys WHERE (ageMonths = :ageMonths And organize=:organize) LIMIT 1")
    suspend fun getWeightForAgeBoysByMonth(ageMonths: Int, organize: Int): WeightForAgeBoys?


    // 头围数据操作
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeadGirls(data: List<HeadForAgeGirls>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeadBoys(data: List<HeadForAgeBoys>)

    @Query("SELECT * FROM head_for_age_girls WHERE ageMonths = :ageMonths")
    suspend fun getHeadForAgeGirls(ageMonths: Int): HeadForAgeGirls?


    @Query("SELECT * FROM head_for_age_girls WHERE (ageMonths = :ageMonths And organize=:organize) LIMIT 1")
    suspend fun getHeadForAgeGirlsByMonth(ageMonths: Int, organize: Int): HeadForAgeGirls?


    @Query("SELECT * FROM head_for_age_boys WHERE ageMonths = :ageMonths")
    suspend fun getHeadForAgeBoys(ageMonths: Int): HeadForAgeBoys?


    @Query("SELECT * FROM head_for_age_boys WHERE (ageMonths = :ageMonths And organize=:organize) LIMIT 1")
    suspend fun getHeadForAgeBoysByMonth(ageMonths: Int, organize: Int): HeadForAgeBoys?

}
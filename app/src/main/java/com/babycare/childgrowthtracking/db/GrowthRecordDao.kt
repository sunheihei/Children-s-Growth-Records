package com.babycare.childgrowthtracking.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.babycare.childgrowthtracking.model.GrowthRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface GrowthRecordDao {

    @Insert
    suspend fun insert(growth: GrowthRecord)

    @Query("SELECT * FROM growth WHERE childId = :childId ORDER BY date ASC")
    fun getByChildId(childId: Int): Flow<List<GrowthRecord>>

    @Query("SELECT * FROM growth WHERE id = :id")
    fun getById(id: Int): Flow<GrowthRecord>


    @Delete
    suspend fun deleteGrowthRecord(growth: GrowthRecord)

    @Update
    suspend fun updateGrowthRecord(growth: GrowthRecord)

}
package com.babycare.childgrowthtracking.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.ChildWithLatestRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface ChildrenDao {

    @Query("SELECT * FROM children")
    fun getChildren(): Flow<List<Child>>

    @Query("SELECT * FROM children WHERE id = :id")
    fun getChildById(id: Int): Flow<Child>


    @Query("SELECT EXISTS(SELECT 1 FROM children WHERE id = :id LIMIT 1)")
    fun isSaved(id: Int): Flow<Boolean>


    @Insert
    suspend fun addChild(child: Child): Long

    @Delete
    suspend fun deleteChild(child: Child)


    // 添加更新方法 - 方法1：通过实体
    @Update
    suspend fun updateChild(child: Child)


    @Query(
        """
        SELECT 
            c.*,
            gr.id AS growth_id,
            gr.childId AS growth_childId,
            gr.date AS growth_date,
            gr.heightCm AS growth_heightCm,
            gr.heightFt AS growth_heightFt,
            gr.heightIn AS growth_heightIn,
            gr.weightKg AS growth_weightKg,
            gr.weightLb AS growth_weightLb,
            gr.headCircleCm AS growth_headCircleCm,
            gr.headCircleIn AS growth_headCircleIn,
            gr.note AS growth_note
        FROM children c
        LEFT JOIN growth gr ON c.id = gr.childId
        WHERE gr.id = (
            SELECT id 
            FROM growth 
            WHERE childId = c.id 
            ORDER BY date DESC 
            LIMIT 1
        ) OR gr.id IS NULL
    """
    )
    fun getChildrenWithLatestRecord(): Flow<List<ChildWithLatestRecord>>
}
package com.babycare.childgrowthtracking.ui.growthrecord

import com.babycare.childgrowthtracking.db.GrowthRecordDao
import com.babycare.childgrowthtracking.model.GrowthRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GrowthRepository @Inject constructor(private val growthDataDao: GrowthRecordDao) {

    suspend fun getGrowthRecordListByChildId(childId: Int): Flow<List<GrowthRecord>> {
        return growthDataDao.getByChildId(childId).flowOn(Dispatchers.IO)
    }

    suspend fun getGrowthRecordsById(id: Int): Flow<GrowthRecord> {
        return growthDataDao.getById(id).flowOn(Dispatchers.IO)
    }


    suspend fun addGrowthRecord(growthRecord: GrowthRecord) {
        growthDataDao.insert(growthRecord)
    }

    suspend fun deleteGrowthRecord(growthRecord: GrowthRecord) {
        growthDataDao.deleteGrowthRecord(growthRecord)
    }

    suspend fun updateGrowthRecord(growthRecord: GrowthRecord) {
        growthDataDao.updateGrowthRecord(growthRecord)
    }

}
package com.babycare.childgrowthtracking.ui.children

import com.babycare.childgrowthtracking.db.ChildrenDao
import com.babycare.childgrowthtracking.model.Child
import com.babycare.childgrowthtracking.model.ChildWithLatestRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChildrenRepository @Inject constructor(private val childrenDao: ChildrenDao) {

    fun getChildren(): Flow<List<Child>> {
        return childrenDao.getChildren().flowOn(Dispatchers.IO)
    }


    fun getChildById(id: Int): Flow<Child> {
        return childrenDao.getChildById(id)
    }

    suspend fun addChildren(child: Child) {
        childrenDao.addChild(child)
    }

    suspend fun deleteChildren(child: Child) {
        childrenDao.deleteChild(child)
    }

    suspend fun updateChildren(child: Child) {
        childrenDao.updateChild(child)
    }

    fun getChildrenWithLatestRecord(): Flow<List<ChildWithLatestRecord>> {
        return childrenDao.getChildrenWithLatestRecord()
    }

}
package com.suji.accountbook.data.repository

import com.suji.accountbook.data.local.dao.PendingRecordDao
import com.suji.accountbook.data.local.entity.PendingRecordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PendingRecordRepository @Inject constructor(
    private val pendingRecordDao: PendingRecordDao
) {
    fun getUnprocessedPendingRecords(): Flow<List<PendingRecordEntity>> {
        return pendingRecordDao.getUnprocessedPendingRecords()
    }

    fun getAllPendingRecords(): Flow<List<PendingRecordEntity>> {
        return pendingRecordDao.getAllPendingRecords()
    }

    suspend fun getPendingRecordById(id: Long): PendingRecordEntity? {
        return pendingRecordDao.getPendingRecordById(id)
    }

    suspend fun insertPendingRecord(pendingRecord: PendingRecordEntity): Long {
        return pendingRecordDao.insertPendingRecord(pendingRecord)
    }

    suspend fun updatePendingRecord(pendingRecord: PendingRecordEntity) {
        pendingRecordDao.updatePendingRecord(pendingRecord)
    }

    suspend fun deletePendingRecord(pendingRecord: PendingRecordEntity) {
        pendingRecordDao.deletePendingRecord(pendingRecord)
    }

    suspend fun deleteProcessedRecords() {
        pendingRecordDao.deleteProcessedRecords()
    }
}

package com.suji.accountbook.data.local.dao

import androidx.room.*
import com.suji.accountbook.data.local.entity.PendingRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PendingRecordDao {
    @Query("SELECT * FROM pending_records WHERE isProcessed = 0 ORDER BY detectedTime DESC")
    fun getUnprocessedPendingRecords(): Flow<List<PendingRecordEntity>>

    @Query("SELECT * FROM pending_records ORDER BY detectedTime DESC")
    fun getAllPendingRecords(): Flow<List<PendingRecordEntity>>

    @Query("SELECT * FROM pending_records WHERE id = :id")
    suspend fun getPendingRecordById(id: Long): PendingRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingRecord(pendingRecord: PendingRecordEntity): Long

    @Update
    suspend fun updatePendingRecord(pendingRecord: PendingRecordEntity)

    @Delete
    suspend fun deletePendingRecord(pendingRecord: PendingRecordEntity)

    @Query("DELETE FROM pending_records WHERE isProcessed = 1")
    suspend fun deleteProcessedRecords()
}

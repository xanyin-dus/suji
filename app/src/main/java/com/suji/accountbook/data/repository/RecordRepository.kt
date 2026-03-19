package com.suji.accountbook.data.repository

import com.suji.accountbook.data.local.dao.CategoryStatistic
import com.suji.accountbook.data.local.dao.DailyStatistic
import com.suji.accountbook.data.local.dao.MonthlyStatistic
import com.suji.accountbook.data.local.dao.RecordDao
import com.suji.accountbook.data.local.entity.RecordEntity
import com.suji.accountbook.data.local.entity.RecordType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecordRepository @Inject constructor(
    private val recordDao: RecordDao
) {
    fun getRecordsByAccountBook(accountBookId: Long): Flow<List<RecordEntity>> {
        return recordDao.getRecordsByAccountBook(accountBookId)
    }

    fun getRecordsByDateRange(accountBookId: Long, startTime: Long, endTime: Long): Flow<List<RecordEntity>> {
        return recordDao.getRecordsByDateRange(accountBookId, startTime, endTime)
    }

    suspend fun getRecordById(id: Long): RecordEntity? {
        return recordDao.getRecordById(id)
    }

    fun getTotalByType(accountBookId: Long, type: RecordType, startTime: Long, endTime: Long): Flow<Double?> {
        return recordDao.getTotalByType(accountBookId, type, startTime, endTime)
    }

    fun getTotalByType(accountBookId: Long, type: RecordType): Flow<Double?> {
        return recordDao.getTotalByType(accountBookId, type)
    }

    fun getCategoryStatistics(accountBookId: Long, type: RecordType, startTime: Long, endTime: Long): Flow<List<CategoryStatistic>> {
        return recordDao.getCategoryStatistics(accountBookId, type, startTime, endTime)
    }

    fun getMonthlyStatistics(accountBookId: Long, startTime: Long, endTime: Long): Flow<List<MonthlyStatistic>> {
        return recordDao.getMonthlyStatistics(accountBookId, startTime, endTime)
    }

    fun getDailyStatistics(accountBookId: Long, startTime: Long, endTime: Long): Flow<List<DailyStatistic>> {
        return recordDao.getDailyStatistics(accountBookId, startTime, endTime)
    }

    suspend fun insertRecord(record: RecordEntity): Long {
        return recordDao.insertRecord(record)
    }

    suspend fun updateRecord(record: RecordEntity) {
        recordDao.updateRecord(record)
    }

    suspend fun deleteRecord(record: RecordEntity) {
        recordDao.deleteRecord(record)
    }

    suspend fun deleteRecordsByAccountBook(accountBookId: Long) {
        recordDao.deleteRecordsByAccountBook(accountBookId)
    }
}

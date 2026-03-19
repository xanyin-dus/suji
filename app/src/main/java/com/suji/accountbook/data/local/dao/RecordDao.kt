package com.suji.accountbook.data.local.dao

import androidx.room.*
import com.suji.accountbook.data.local.entity.RecordEntity
import com.suji.accountbook.data.local.entity.RecordType
import kotlinx.coroutines.flow.Flow

@Dao
interface RecordDao {
    @Query("SELECT * FROM records WHERE accountBookId = :accountBookId ORDER BY date DESC")
    fun getRecordsByAccountBook(accountBookId: Long): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE accountBookId = :accountBookId AND date BETWEEN :startTime AND :endTime ORDER BY date DESC")
    fun getRecordsByDateRange(accountBookId: Long, startTime: Long, endTime: Long): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE id = :id")
    suspend fun getRecordById(id: Long): RecordEntity?

    @Query("SELECT SUM(amount) FROM records WHERE accountBookId = :accountBookId AND type = :type AND date BETWEEN :startTime AND :endTime")
    fun getTotalByType(accountBookId: Long, type: RecordType, startTime: Long, endTime: Long): Flow<Double?>

    @Query("SELECT SUM(amount) FROM records WHERE accountBookId = :accountBookId AND type = :type")
    fun getTotalByType(accountBookId: Long, type: RecordType): Flow<Double?>

    @Query("""
        SELECT c.name as categoryName, c.color as categoryColor, SUM(r.amount) as totalAmount
        FROM records r
        LEFT JOIN categories c ON r.categoryId = c.id
        WHERE r.accountBookId = :accountBookId 
        AND r.type = :type 
        AND r.date BETWEEN :startTime AND :endTime
        GROUP BY r.categoryId
        ORDER BY totalAmount DESC
    """)
    fun getCategoryStatistics(accountBookId: Long, type: RecordType, startTime: Long, endTime: Long): Flow<List<CategoryStatistic>>

    @Query("""
        SELECT strftime('%Y-%m', date/1000, 'unixepoch') as month, 
               SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as income,
               SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as expense
        FROM records
        WHERE accountBookId = :accountBookId
        AND date BETWEEN :startTime AND :endTime
        GROUP BY month
        ORDER BY month ASC
    """)
    fun getMonthlyStatistics(accountBookId: Long, startTime: Long, endTime: Long): Flow<List<MonthlyStatistic>>

    @Query("""
        SELECT strftime('%Y-%m-%d', date/1000, 'unixepoch') as day,
               SUM(CASE WHEN type = 'INCOME' THEN amount ELSE 0 END) as income,
               SUM(CASE WHEN type = 'EXPENSE' THEN amount ELSE 0 END) as expense
        FROM records
        WHERE accountBookId = :accountBookId
        AND date BETWEEN :startTime AND :endTime
        GROUP BY day
        ORDER BY day ASC
    """)
    fun getDailyStatistics(accountBookId: Long, startTime: Long, endTime: Long): Flow<List<DailyStatistic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity): Long

    @Update
    suspend fun updateRecord(record: RecordEntity)

    @Delete
    suspend fun deleteRecord(record: RecordEntity)

    @Query("DELETE FROM records WHERE accountBookId = :accountBookId")
    suspend fun deleteRecordsByAccountBook(accountBookId: Long)
}

data class CategoryStatistic(
    val categoryName: String?,
    val categoryColor: String?,
    val totalAmount: Double
)

data class MonthlyStatistic(
    val month: String,
    val income: Double,
    val expense: Double
)

data class DailyStatistic(
    val day: String,
    val income: Double,
    val expense: Double
)

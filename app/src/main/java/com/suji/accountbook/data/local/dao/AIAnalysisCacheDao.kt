package com.suji.accountbook.data.local.dao

import androidx.room.*
import com.suji.accountbook.data.local.entity.AIAnalysisCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AIAnalysisCacheDao {
    @Query("SELECT * FROM ai_analysis_cache ORDER BY createdAt DESC")
    fun getAllCache(): Flow<List<AIAnalysisCacheEntity>>

    @Query("SELECT * FROM ai_analysis_cache WHERE analysisType = :type AND dateRangeStart = :start AND dateRangeEnd = :end LIMIT 1")
    suspend fun getCacheByTypeAndRange(type: String, start: Long, end: Long): AIAnalysisCacheEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: AIAnalysisCacheEntity): Long

    @Delete
    suspend fun deleteCache(cache: AIAnalysisCacheEntity)

    @Query("DELETE FROM ai_analysis_cache")
    suspend fun deleteAllCache()
}

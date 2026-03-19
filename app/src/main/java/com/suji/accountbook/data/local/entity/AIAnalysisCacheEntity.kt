package com.suji.accountbook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_analysis_cache")
data class AIAnalysisCacheEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val analysisType: String,
    val dateRangeStart: Long,
    val dateRangeEnd: Long,
    val analysisResult: String,
    val createdAt: Long = System.currentTimeMillis()
)

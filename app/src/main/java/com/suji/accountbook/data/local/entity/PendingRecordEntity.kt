package com.suji.accountbook.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pending_records")
data class PendingRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val amount: Double,
    val source: String,
    val detectedText: String,
    val detectedTime: Long,
    val isProcessed: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

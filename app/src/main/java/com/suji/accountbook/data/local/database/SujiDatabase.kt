package com.suji.accountbook.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.suji.accountbook.data.local.dao.*
import com.suji.accountbook.data.local.entity.*

@Database(
    entities = [
        AccountBookEntity::class,
        CategoryEntity::class,
        RecordEntity::class,
        PendingRecordEntity::class,
        AIAnalysisCacheEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class SujiDatabase : RoomDatabase() {
    abstract fun accountBookDao(): AccountBookDao
    abstract fun categoryDao(): CategoryDao
    abstract fun recordDao(): RecordDao
    abstract fun pendingRecordDao(): PendingRecordDao
    abstract fun aiAnalysisCacheDao(): AIAnalysisCacheDao
}

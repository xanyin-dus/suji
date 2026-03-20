package com.suji.accountbook.data.local.database

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): SujiDatabase {
        return Room.databaseBuilder(
            context,
            SujiDatabase::class.java,
            "suji_database"
        ).build()
    }

    @Provides
    fun provideAccountBookDao(database: SujiDatabase) = database.accountBookDao()

    @Provides
    fun provideCategoryDao(database: SujiDatabase) = database.categoryDao()

    @Provides
    fun provideRecordDao(database: SujiDatabase) = database.recordDao()

    @Provides
    fun providePendingRecordDao(database: SujiDatabase) = database.pendingRecordDao()

    @Provides
    fun provideAIAnalysisCacheDao(database: SujiDatabase) = database.aiAnalysisCacheDao()
}

package com.suji.accountbook.util

import android.content.Context
import com.google.gson.Gson
import com.suji.accountbook.data.local.entity.AccountBookEntity
import com.suji.accountbook.data.local.entity.CategoryEntity
import com.suji.accountbook.data.local.entity.RecordEntity
import com.suji.accountbook.data.repository.AccountBookRepository
import com.suji.accountbook.data.repository.CategoryRepository
import com.suji.accountbook.data.repository.RecordRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

data class BackupData(
    val accountBooks: List<AccountBookEntity>,
    val categories: List<CategoryEntity>,
    val records: List<RecordEntity>,
    val backupTime: Long = System.currentTimeMillis()
)

@Singleton
class DataBackupManager @Inject constructor(
    @ApplicationContext private val context: Context,
    private val accountBookRepository: AccountBookRepository,
    private val categoryRepository: CategoryRepository,
    private val recordRepository: RecordRepository,
    private val gson: Gson
) {
    private val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())

    suspend fun backupData(): Result<File> = withContext(Dispatchers.IO) {
        try {
            val accountBooks = mutableListOf<AccountBookEntity>()
            val categories = mutableListOf<CategoryEntity>()
            val records = mutableListOf<RecordEntity>()

            accountBookRepository.getAllAccountBooks().collect {
                accountBooks.addAll(it)
            }

            val backupData = BackupData(
                accountBooks = accountBooks,
                categories = categories,
                records = records
            )

            val fileName = "suji_backup_${dateFormat.format(Date())}.json"
            val file = File(context.cacheDir, fileName)
            file.writeText(gson.toJson(backupData))

            Result.success(file)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restoreData(file: File): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val json = file.readText()
            val backupData = gson.fromJson(json, BackupData::class.java)

            backupData.accountBooks.forEach { accountBookRepository.insertAccountBook(it) }
            backupData.categories.forEach { categoryRepository.insertCategory(it) }
            backupData.records.forEach { recordRepository.insertRecord(it) }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

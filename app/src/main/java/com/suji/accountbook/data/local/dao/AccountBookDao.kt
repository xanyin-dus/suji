package com.suji.accountbook.data.local.dao

import androidx.room.*
import com.suji.accountbook.data.local.entity.AccountBookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AccountBookDao {
    @Query("SELECT * FROM account_books ORDER BY createdAt DESC")
    fun getAllAccountBooks(): Flow<List<AccountBookEntity>>

    @Query("SELECT * FROM account_books WHERE id = :id")
    suspend fun getAccountBookById(id: Long): AccountBookEntity?

    @Query("SELECT * FROM account_books WHERE isDefault = 1 LIMIT 1")
    suspend fun getDefaultAccountBook(): AccountBookEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAccountBook(accountBook: AccountBookEntity): Long

    @Update
    suspend fun updateAccountBook(accountBook: AccountBookEntity)

    @Delete
    suspend fun deleteAccountBook(accountBook: AccountBookEntity)

    @Query("UPDATE account_books SET isDefault = 0")
    suspend fun clearDefaultAccountBook()

    @Transaction
    suspend fun setDefaultAccountBook(id: Long) {
        clearDefaultAccountBook()
        getAccountBookById(id)?.let { book ->
            updateAccountBook(book.copy(isDefault = true))
        }
    }
}

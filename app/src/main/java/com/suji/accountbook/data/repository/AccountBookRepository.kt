package com.suji.accountbook.data.repository

import com.suji.accountbook.data.local.dao.AccountBookDao
import com.suji.accountbook.data.local.entity.AccountBookEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountBookRepository @Inject constructor(
    private val accountBookDao: AccountBookDao
) {
    fun getAllAccountBooks(): Flow<List<AccountBookEntity>> {
        return accountBookDao.getAllAccountBooks()
    }

    suspend fun getAccountBookById(id: Long): AccountBookEntity? {
        return accountBookDao.getAccountBookById(id)
    }

    suspend fun getDefaultAccountBook(): AccountBookEntity? {
        return accountBookDao.getDefaultAccountBook()
    }

    suspend fun insertAccountBook(accountBook: AccountBookEntity): Long {
        return accountBookDao.insertAccountBook(accountBook)
    }

    suspend fun updateAccountBook(accountBook: AccountBookEntity) {
        accountBookDao.updateAccountBook(accountBook)
    }

    suspend fun deleteAccountBook(accountBook: AccountBookEntity) {
        accountBookDao.deleteAccountBook(accountBook)
    }

    suspend fun setDefaultAccountBook(id: Long) {
        accountBookDao.setDefaultAccountBook(id)
    }
}

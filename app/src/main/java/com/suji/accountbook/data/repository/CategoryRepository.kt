package com.suji.accountbook.data.repository

import com.suji.accountbook.data.local.dao.CategoryDao
import com.suji.accountbook.data.local.entity.CategoryEntity
import com.suji.accountbook.data.local.entity.CategoryType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao
) {
    fun getCategoriesByAccountBook(accountBookId: Long): Flow<List<CategoryEntity>> {
        return categoryDao.getCategoriesByAccountBook(accountBookId)
    }

    fun getCategoriesByType(accountBookId: Long, type: CategoryType): Flow<List<CategoryEntity>> {
        return categoryDao.getCategoriesByType(accountBookId, type)
    }

    suspend fun getCategoryById(id: Long): CategoryEntity? {
        return categoryDao.getCategoryById(id)
    }

    suspend fun insertCategory(category: CategoryEntity): Long {
        return categoryDao.insertCategory(category)
    }

    suspend fun insertCategories(categories: List<CategoryEntity>) {
        categoryDao.insertCategories(categories)
    }

    suspend fun updateCategory(category: CategoryEntity) {
        categoryDao.updateCategory(category)
    }

    suspend fun deleteCategory(category: CategoryEntity) {
        categoryDao.deleteCategory(category)
    }

    suspend fun deleteCategoriesByAccountBook(accountBookId: Long) {
        categoryDao.deleteCategoriesByAccountBook(accountBookId)
    }
}

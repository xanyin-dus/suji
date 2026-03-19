package com.suji.accountbook.data.repository

import com.suji.accountbook.data.local.dao.CategoryDao
import com.suji.accountbook.data.local.entity.CategoryEntity
import com.suji.accountbook.data.local.entity.DefaultCategories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryInitializer @Inject constructor(
    private val categoryDao: CategoryDao
) {
    suspend fun initializeDefaultCategories(accountBookId: Long) = withContext(Dispatchers.IO) {
        val existingCategories = categoryDao.getCategoriesByAccountBookOnce(accountBookId)
        if (existingCategories.isEmpty()) {
            val defaultCategories = DefaultCategories.getAllCategories().mapIndexed { index, default ->
                CategoryEntity(
                    accountBookId = accountBookId,
                    name = default.name,
                    icon = default.icon,
                    color = default.color,
                    type = default.type,
                    sortOrder = index
                )
            }
            categoryDao.insertCategories(defaultCategories)
        }
    }
}

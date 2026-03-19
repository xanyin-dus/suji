package com.suji.accountbook.data.local.dao

import androidx.room.*
import com.suji.accountbook.data.local.entity.CategoryEntity
import com.suji.accountbook.data.local.entity.CategoryType
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE accountBookId = :accountBookId ORDER BY sortOrder ASC")
    fun getCategoriesByAccountBook(accountBookId: Long): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE accountBookId = :accountBookId AND type = :type ORDER BY sortOrder ASC")
    fun getCategoriesByType(accountBookId: Long, type: CategoryType): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id")
    suspend fun getCategoryById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<CategoryEntity>)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE accountBookId = :accountBookId")
    suspend fun deleteCategoriesByAccountBook(accountBookId: Long)

    @Query("SELECT * FROM categories WHERE accountBookId = :accountBookId ORDER BY sortOrder ASC")
    suspend fun getCategoriesByAccountBookOnce(accountBookId: Long): List<CategoryEntity>
}


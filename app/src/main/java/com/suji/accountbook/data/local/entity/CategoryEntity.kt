package com.suji.accountbook.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = AccountBookEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountBookId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("accountBookId")]
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountBookId: Long,
    val name: String,
    val icon: String,
    val color: String,
    val type: CategoryType,
    val sortOrder: Int = 0,
    val isDefault: Boolean = false
)

enum class CategoryType {
    INCOME, EXPENSE
}

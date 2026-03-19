package com.suji.accountbook.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "records",
    foreignKeys = [
        ForeignKey(
            entity = AccountBookEntity::class,
            parentColumns = ["id"],
            childColumns = ["accountBookId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("accountBookId"), Index("categoryId"), Index("date")]
)
data class RecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val accountBookId: Long,
    val categoryId: Long?,
    val amount: Double,
    val type: RecordType,
    val remark: String = "",
    val date: Long,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

enum class RecordType {
    INCOME, EXPENSE
}

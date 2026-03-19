package com.suji.accountbook.data.local.entity

data class DefaultCategory(
    val name: String,
    val icon: String,
    val color: String,
    val type: CategoryType
)

object DefaultCategories {
    val expenseCategories = listOf(
        DefaultCategory("餐饮", "🍜", "#FF6B6B", CategoryType.EXPENSE),
        DefaultCategory("交通", "🚗", "#4ECDC4", CategoryType.EXPENSE),
        DefaultCategory("购物", "🛒", "#45B7D1", CategoryType.EXPENSE),
        DefaultCategory("娱乐", "🎮", "#96CEB4", CategoryType.EXPENSE),
        DefaultCategory("居住", "🏠", "#FFEAA7", CategoryType.EXPENSE),
        DefaultCategory("通讯", "📱", "#DDA0DD", CategoryType.EXPENSE),
        DefaultCategory("医疗", "💊", "#98D8C8", CategoryType.EXPENSE),
        DefaultCategory("教育", "📚", "#F7DC6F", CategoryType.EXPENSE),
        DefaultCategory("服饰", "👔", "#BB8FCE", CategoryType.EXPENSE),
        DefaultCategory("美容", "💄", "#F8B500", CategoryType.EXPENSE),
        DefaultCategory("社交", "🎁", "#E8DAEF", CategoryType.EXPENSE),
        DefaultCategory("旅行", "✈️", "#85C1E9", CategoryType.EXPENSE),
        DefaultCategory("宠物", "🐾", "#F9E79F", CategoryType.EXPENSE),
        DefaultCategory("办公", "💼", "#AED6F1", CategoryType.EXPENSE),
        DefaultCategory("其他", "📝", "#D5D8DC", CategoryType.EXPENSE)
    )

    val incomeCategories = listOf(
        DefaultCategory("工资", "💰", "#2ECC71", CategoryType.INCOME),
        DefaultCategory("奖金", "🎉", "#27AE60", CategoryType.INCOME),
        DefaultCategory("投资", "📈", "#3498DB", CategoryType.INCOME),
        DefaultCategory("兼职", "💼", "#9B59B6", CategoryType.INCOME),
        DefaultCategory("红包", "🧧", "#E74C3C", CategoryType.INCOME),
        DefaultCategory("退款", "↩️", "#1ABC9C", CategoryType.INCOME),
        DefaultCategory("其他", "📝", "#95A5A6", CategoryType.INCOME)
    )

    fun getAllCategories(): List<DefaultCategory> {
        return expenseCategories + incomeCategories
    }
}

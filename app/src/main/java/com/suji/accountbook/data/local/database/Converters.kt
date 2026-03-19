package com.suji.accountbook.data.local.database

import androidx.room.TypeConverter
import com.suji.accountbook.data.local.entity.CategoryType
import com.suji.accountbook.data.local.entity.RecordType

class Converters {
    @TypeConverter
    fun fromCategoryType(value: CategoryType): String {
        return value.name
    }

    @TypeConverter
    fun toCategoryType(value: String): CategoryType {
        return CategoryType.valueOf(value)
    }

    @TypeConverter
    fun fromRecordType(value: RecordType): String {
        return value.name
    }

    @TypeConverter
    fun toRecordType(value: String): RecordType {
        return RecordType.valueOf(value)
    }
}

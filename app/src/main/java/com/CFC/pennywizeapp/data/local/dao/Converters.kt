package com.CFC.pennywizeapp.data.local.dao

import androidx.room.TypeConverter
import com.CFC.pennywizeapp.models.CategoryType
import com.CFC.pennywizeapp.models.EntryType

class Converters {

    @TypeConverter
    fun fromEntryType(value: EntryType): String = value.name

    @TypeConverter
    fun toEntryType(value: String): EntryType = EntryType.valueOf(value)

    @TypeConverter
    fun fromCategoryType(value: CategoryType): String = value.name

    @TypeConverter
    fun toCategoryType(value: String): CategoryType = CategoryType.valueOf(value)
}
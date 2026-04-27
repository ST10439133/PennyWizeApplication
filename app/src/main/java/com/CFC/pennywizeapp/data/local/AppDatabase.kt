package com.CFC.pennywizeapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.CFC.pennywizeapp.data.local.dao.CategoryDao
import com.CFC.pennywizeapp.data.local.dao.Converters
import com.CFC.pennywizeapp.data.local.dao.EntryDao
import com.CFC.pennywizeapp.models.Category
import com.CFC.pennywizeapp.models.Entry

@Database(
    entities = [Category::class, Entry::class],
    version = 1
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    abstract fun categoryDao(): CategoryDao
}
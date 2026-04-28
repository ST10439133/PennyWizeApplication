package com.CFC.pennywizeapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.CFC.pennywizeapp.data.local.dao.BudgetDao
import com.CFC.pennywizeapp.data.local.dao.CategoryDao
import com.CFC.pennywizeapp.data.local.dao.Converters
import com.CFC.pennywizeapp.data.local.dao.EntryDao
import com.CFC.pennywizeapp.models.BudgetEntity
import com.CFC.pennywizeapp.models.Category
import com.CFC.pennywizeapp.models.Entry

@Database(
    entities = [Category::class, Entry::class, BudgetEntity::class],
    version = 3,  // Increment to version 3 for budget table
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun entryDao(): EntryDao
    abstract fun categoryDao(): CategoryDao
    abstract fun budgetDao(): BudgetDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 2 to 3 (add budget table)
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create budgets table
                database.execSQL("""
                    CREATE TABLE IF NOT EXISTS budgets (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        userId TEXT NOT NULL,
                        minGoal REAL NOT NULL,
                        maxGoal REAL NOT NULL,
                        currentTotal REAL NOT NULL
                    )
                """)

                // Create index for faster user queries
                database.execSQL("CREATE INDEX IF NOT EXISTS index_budgets_userId ON budgets(userId)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pennywize_db"
                )
                    .addMigrations(MIGRATION_2_3)  // Add the new migration
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
package com.CFC.pennywizeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.CFC.pennywizeapp.models.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertCategory(category: Category)

    @Query("SELECT * FROM categories")
    fun getAllCategories(): Flow<List<Category>>
}
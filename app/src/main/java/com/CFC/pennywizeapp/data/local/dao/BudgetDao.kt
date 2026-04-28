package com.CFC.pennywizeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.CFC.pennywizeapp.models.BudgetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BudgetDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertOrUpdateBudget(budget: BudgetEntity)

    @Query("SELECT * FROM budgets WHERE userId = :userId LIMIT 1")
    suspend fun getBudgetByUserId(userId: String): BudgetEntity?

    @Query("SELECT * FROM budgets WHERE userId = :userId")
    fun observeBudgetByUserId(userId: String): Flow<BudgetEntity?>

    @Query("DELETE FROM budgets WHERE userId = :userId")
    suspend fun deleteBudgetByUserId(userId: String)
}
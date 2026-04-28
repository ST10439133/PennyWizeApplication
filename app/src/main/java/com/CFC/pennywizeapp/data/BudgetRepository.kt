package com.CFC.pennywizeapp.data

import android.content.Context
import com.CFC.pennywizeapp.data.local.DatabaseProvider
import com.CFC.pennywizeapp.models.BudgetEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Budget Repository - Manages budget goals per user
 * BACKED BY ROOM DATABASE with USER ISOLATION
 */
class BudgetRepository private constructor(private val context: Context) {

    private val database = DatabaseProvider.getDatabase(context)
    private val budgetDao = database.budgetDao()

    private var currentUserId: String = ""
    private var currentJob: kotlinx.coroutines.Job? = null

    // Budget data for current user (nullable because user may not have saved goals yet)
    private val _budget = MutableStateFlow<BudgetEntity?>(null)
    val budget: StateFlow<BudgetEntity?> = _budget.asStateFlow()

    /**
     * Set the current logged-in user and load their budget
     * Call this after successful login
     */
    fun setCurrentUser(userId: String) {
        if (userId.isEmpty()) return
        if (currentUserId == userId) return

        // Cancel previous collection job
        currentJob?.cancel()

        currentUserId = userId

        // Load budget for this user
        currentJob = CoroutineScope(Dispatchers.IO).launch {
            budgetDao.observeBudgetByUserId(userId)
                .catch { e -> e.printStackTrace() }
                .collect { budgetEntity ->
                    _budget.value = budgetEntity ?: BudgetEntity(
                        userId = userId,
                        minGoal = 500.0,
                        maxGoal = 2000.0,
                        currentTotal = 0.0
                    )
                }
        }
    }

    /**
     * Clear current user data (on logout)
     */
    fun clearCurrentUser() {
        currentJob?.cancel()
        currentUserId = ""
        _budget.value = null
    }

    /**
     * Get current user ID
     */
    fun getCurrentUserId(): String = currentUserId

    /**
     * Update budget goals for current user
     */
    fun updateGoals(min: Double, max: Double) {
        if (currentUserId.isEmpty()) {
            println("BudgetRepository: Cannot update goals - no user logged in")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingBudget = budgetDao.getBudgetByUserId(currentUserId)
                val budget = BudgetEntity(
                    id = existingBudget?.id ?: 0,
                    userId = currentUserId,
                    minGoal = min,
                    maxGoal = max,
                    currentTotal = existingBudget?.currentTotal ?: 0.0
                )
                budgetDao.insertOrUpdateBudget(budget)
                println("BudgetRepository: Updated goals for user $currentUserId: min=$min, max=$max")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Update current total expenses for the budget
     * Called automatically when expenses change
     */
    fun updateCurrentTotal(total: Double) {
        if (currentUserId.isEmpty()) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val existingBudget = budgetDao.getBudgetByUserId(currentUserId)
                if (existingBudget != null) {
                    val updatedBudget = existingBudget.copy(currentTotal = total)
                    budgetDao.insertOrUpdateBudget(updatedBudget)
                } else {
                    // Create default budget if none exists
                    val defaultBudget = BudgetEntity(
                        userId = currentUserId,
                        minGoal = 500.0,
                        maxGoal = 2000.0,
                        currentTotal = total
                    )
                    budgetDao.insertOrUpdateBudget(defaultBudget)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        @Volatile
        private var instance: BudgetRepository? = null

        fun getInstance(context: Context): BudgetRepository {
            return instance ?: synchronized(this) {
                instance ?: BudgetRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
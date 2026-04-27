package com.CFC.pennywizeapp.data

import com.CFC.pennywizeapp.models.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object BudgetRepository {
    private val _budget = MutableStateFlow(Budget(500.0, 2000.0, 0.0))
    val budget: StateFlow<Budget> = _budget

    fun updateGoals(min: Double, max: Double) {
        _budget.value = _budget.value.copy(minGoal = min, maxGoal = max)
    }

    fun updateCurrentTotal(total: Double) {
        _budget.value = _budget.value.copy(currentTotal = total)
    }
}
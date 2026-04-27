package com.CFC.pennywizeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.CFC.pennywizeapp.data.BudgetRepository
import com.CFC.pennywizeapp.data.EntryRepository
import com.CFC.pennywizeapp.models.EntryType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class BudgetGoalsViewModel : ViewModel() {
    val budgetState = BudgetRepository.budget

    val totalIncome: StateFlow<Double> = EntryRepository.entries.map { entries ->
        entries.filter { it.type == EntryType.INCOME }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // CLEAN VERSION: Only calculates the sum
    val totalExpenses: StateFlow<Double> = EntryRepository.entries.map { entries ->
        entries.filter { it.type == EntryType.EXPENSE }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    fun saveGoals(min: Double, max: Double) {
        BudgetRepository.updateGoals(min, max)
    }
}
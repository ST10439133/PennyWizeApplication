package com.CFC.pennywizeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.CFC.pennywizeapp.data.BudgetRepository
import com.CFC.pennywizeapp.data.EntryRepository
import com.CFC.pennywizeapp.models.BudgetEntity
import com.CFC.pennywizeapp.models.EntryType
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BudgetGoalsViewModel(
    private val entryRepository: EntryRepository,
    private val budgetRepository: BudgetRepository
) : ViewModel() {

    // Observe budget from repository (user-specific)
    val budgetState: StateFlow<BudgetEntity?> = budgetRepository.budget

    // Calculate total income from entries (already user-filtered by EntryRepository)
    val totalIncome: StateFlow<Double> = entryRepository.entries.map { entries ->
        entries.filter { it.type == EntryType.INCOME }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    // Calculate total expenses from entries and update budget
    val totalExpenses: StateFlow<Double> = entryRepository.entries.map { entries ->
        entries.filter { it.type == EntryType.EXPENSE }.sumOf { it.amount }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    init {
        // Update budget current total whenever expenses change
        viewModelScope.launch {
            totalExpenses.collect { expenses ->
                budgetRepository.updateCurrentTotal(expenses)
            }
        }
    }

    fun saveGoals(min: Double, max: Double) {
        budgetRepository.updateGoals(min, max)
    }
}
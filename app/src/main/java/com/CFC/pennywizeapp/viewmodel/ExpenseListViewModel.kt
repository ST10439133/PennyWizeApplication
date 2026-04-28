package com.CFC.pennywizeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.CFC.pennywizeapp.data.EntryRepository
import com.CFC.pennywizeapp.data.CategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import java.util.Calendar

class ExpenseListViewModel(
    private val entryRepository: EntryRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _selectedDate = MutableStateFlow<Long?>(null)

    val filteredExpenses = combine(
        entryRepository.entries,
        categoryRepository.categories,
        _selectedDate
    ) { entries, categories, selectedDate ->
        if (selectedDate == null) {
            emptyList()
        } else {
            val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            val startOfDay = calendar.timeInMillis

            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            val endOfDay = calendar.timeInMillis

            entries.filter { it.timestamp in startOfDay..endOfDay }
                .map { entry ->
                    val name = categories.find { it.id == entry.categoryId }?.name ?: "Unknown"
                    entry to name
                }
        }
    }

    fun filterByDate(dateMillis: Long?) {
        _selectedDate.value = dateMillis
    }
}
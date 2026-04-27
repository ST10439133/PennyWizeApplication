package com.CFC.pennywizeapp.viewmodels

import androidx.lifecycle.ViewModel
import com.CFC.pennywizeapp.data.EntryRepository
import com.CFC.pennywizeapp.data.CategoryRepository
import com.CFC.pennywizeapp.models.CategoryTotal
import com.CFC.pennywizeapp.models.Entry
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar

class ExpenseListViewModel : ViewModel() {
    // Stores the selected date in milliseconds
    private val _selectedDate = MutableStateFlow<Long?>(null)

    val filteredExpenses = combine(
        EntryRepository.entries,
        CategoryRepository.categories,
        _selectedDate
    ) { entries, categories, selectedDate ->
        if (selectedDate == null) {
            // If no date is selected, show all or none (adjust based on preference)
            emptyList()
        } else {
            // Calculate the start and end of the selected day
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
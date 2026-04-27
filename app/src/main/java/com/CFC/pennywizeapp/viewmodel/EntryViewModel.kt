package com.CFC.pennywizeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.CFC.pennywizeapp.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class EntryViewModel : ViewModel() {

    // Temporary in-memory categories (until Room is connected by teammate)
    private val _categories = MutableStateFlow(
        listOf(
            Category(
                name = "Groceries",
                type = CategoryType.EXPENSE,
                color = "#4CAF50",
                icon = "shopping_cart"
            ),
            Category(
                name = "Transport",
                type = CategoryType.EXPENSE,
                color = "#2196F3",
                icon = "directions_car"
            ),
            Category(
                name = "Salary",
                type = CategoryType.INCOME,
                color = "#FFC107",
                icon = "attach_money"
            )
        )
    )

    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    // Temporary in-memory entry handling
    private val _entries = MutableStateFlow<List<Entry>>(emptyList())
    val entries: StateFlow<List<Entry>> = _entries.asStateFlow()

    fun insertEntry(entry: Entry) {
        _entries.value = _entries.value + entry
        println("Entry added: $entry")
    }
}
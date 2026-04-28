package com.CFC.pennywizeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.CFC.pennywizeapp.data.CategoryRepository
import com.CFC.pennywizeapp.data.EntryRepository
import com.CFC.pennywizeapp.models.Category
import com.CFC.pennywizeapp.models.Entry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class EntryViewModel(
    private val entryRepository: EntryRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    val categories: StateFlow<List<Category>> = categoryRepository.categories
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val entries: StateFlow<List<Entry>> = entryRepository.entries
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun insertEntry(entry: Entry) {
        entryRepository.addEntry(entry)
        println("EntryViewModel: Adding entry for user: ${entryRepository.getCurrentUserId()}")
    }
}
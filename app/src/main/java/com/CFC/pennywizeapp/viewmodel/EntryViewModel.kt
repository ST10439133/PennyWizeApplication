package com.CFC.pennywizeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.CFC.pennywizeapp.data.CategoryRepository
import com.CFC.pennywizeapp.data.EntryRepository
import com.CFC.pennywizeapp.models.Category
import com.CFC.pennywizeapp.models.CategoryType
import com.CFC.pennywizeapp.models.Entry
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    /**
     * Create a new category and automatically refresh the categories list
     * @param name The name of the new category
     * @param type The type of category (INCOME or EXPENSE)
     * @return The newly created Category object
     */
    suspend fun createAndSelectCategory(name: String, type: CategoryType): Category {
        return try {
            val newCategory = categoryRepository.createCategory(name, type)
            // The categories StateFlow will automatically update because CategoryRepository
            // refreshes its internal StateFlow after insertion
            newCategory
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
    }
}
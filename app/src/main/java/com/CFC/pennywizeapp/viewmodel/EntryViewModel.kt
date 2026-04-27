package com.CFC.pennywizeapp.viewmodel

import androidx.lifecycle.ViewModel
import com.CFC.pennywizeapp.data.CategoryRepository
import com.CFC.pennywizeapp.data.EntryRepository
import com.CFC.pennywizeapp.models.Category
import com.CFC.pennywizeapp.models.Entry
import kotlinx.coroutines.flow.StateFlow

class EntryViewModel : ViewModel() {

    // 1. Connect to the Repositories from Project 2
    // We point directly to the Repository flows so the UI stays updated
    val categories: StateFlow<List<Category>> = CategoryRepository.categories
    val entries: StateFlow<List<Entry>> = EntryRepository.entries

    // 2. Update the insert logic
    fun insertEntry(entry: Entry) {
        // This updates the in-memory list in EntryRepository.
        // Later, you will simply update the Repository to save to Room,
        // and this ViewModel code won't have to change!
        EntryRepository.addEntry(entry)

        // Helpful for debugging in the Logcat
        println("ViewModel: Requesting Repository to add entry: ${entry.amount}")
    }
}
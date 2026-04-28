package com.CFC.pennywizeapp.data

import com.CFC.pennywizeapp.models.Entry
import com.CFC.pennywizeapp.models.EntryType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.Calendar

object EntryRepository {
    // Helper function to generate a specific date for testing
    private fun getMockDate(day: Int): Long {
        return Calendar.getInstance().apply {
            set(2026, Calendar.APRIL, day, 12, 0) // April 2026
        }.timeInMillis
    }

    private val _entries = MutableStateFlow<List<Entry>>(listOf(
        // April 27th (Today)
        Entry(
            amount = 150.0,
            categoryId = "1",
            note = "Weekly Shop",
            type = EntryType.EXPENSE,
            timestamp = getMockDate(27)
        ),
        // April 25th
        Entry(
            amount = 50.0,
            categoryId = "2",
            note = "Bus Pass",
            type = EntryType.EXPENSE,
            timestamp = getMockDate(25)
        ),
        // April 20th
        Entry(
            amount = 200.0,
            categoryId = "1",
            note = "Dinner Out",
            type = EntryType.EXPENSE,
            timestamp = getMockDate(20)
        ),
        // April 1st
        Entry(
            amount = 5000.0,
            categoryId = "3",
            note = "Monthly Pay",
            type = EntryType.INCOME,
            timestamp = getMockDate(1)
        )
    ))

    val entries: StateFlow<List<Entry>> = _entries

    fun addEntry(entry: Entry) {
        _entries.value = _entries.value + entry
    }
}
package com.CFC.pennywizeapp.data

import android.content.Context
import android.graphics.Bitmap
import com.CFC.pennywizeapp.data.local.DatabaseProvider
import com.CFC.pennywizeapp.models.Entry
import com.CFC.pennywizeapp.models.EntryType
import com.CFC.pennywizeapp.utils.saveImageToInternalStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.UUID

/**
 * Entry Repository - Manages all entry data operations with image support
 * BACKED BY ROOM DATABASE
 */
object EntryRepository {

    private lateinit var context: Context
    private var isInitialized = false

    private var _entries: Flow<List<Entry>> = flow { emit(emptyList()) }

    val entries: Flow<List<Entry>>
        get() = _entries

    /**
     * Initialize the repository with database context
     */
    fun init(appContext: Context) {
        if (!isInitialized) {
            context = appContext.applicationContext
            val database = DatabaseProvider.getDatabase(context)
            val entryDao = database.entryDao()

            _entries = entryDao.getAllEntries()
                .catch { e -> e.printStackTrace() }
                .stateIn(
                    scope = CoroutineScope(Dispatchers.IO),
                    started = SharingStarted.Eagerly,
                    initialValue = emptyList()
                )
            isInitialized = true

            // Add mock data if database is empty
            CoroutineScope(Dispatchers.IO).launch {
                val existingEntries = entryDao.getAllEntries().first()
                if (existingEntries.isEmpty()) {
                    addMockData()
                }
            }
        }
    }

    /**
     * Add a new entry with optional image
     */
    fun addEntry(
        amount: Double,
        categoryId: String,
        type: EntryType,
        note: String? = null,
        timestamp: Long = System.currentTimeMillis(),
        imageBitmap: Bitmap? = null
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val imagePath = imageBitmap?.let {
                    saveImageToInternalStorage(context, it)
                }

                val entry = Entry(
                    id = UUID.randomUUID().toString(),
                    amount = amount,
                    categoryId = categoryId,
                    note = note,
                    timestamp = timestamp,
                    type = type,
                    attachmentUri = imagePath
                )

                val database = DatabaseProvider.getDatabase(context)
                database.entryDao().insertEntry(entry)

                println("EntryRepository: Entry added with image: ${imagePath != null}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Add an existing Entry object
     */
    fun addEntry(entry: Entry) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = DatabaseProvider.getDatabase(context)
                database.entryDao().insertEntry(entry)
                println("EntryRepository: Entry added: ${entry.amount}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Add multiple entries
     */
    fun addEntries(entries: List<Entry>) {
        CoroutineScope(Dispatchers.IO).launch {
            val database = DatabaseProvider.getDatabase(context)
            database.entryDao().insertEntries(entries)
        }
    }

    /**
     * Delete an entry
     */
    fun deleteEntry(entry: Entry) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = DatabaseProvider.getDatabase(context)
                database.entryDao().deleteEntryById(entry.id)
                println("EntryRepository: Entry deleted")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Delete all entries
     */
    fun deleteAllEntries() {
        CoroutineScope(Dispatchers.IO).launch {
            val database = DatabaseProvider.getDatabase(context)
            database.entryDao().deleteAllEntries()
        }
    }

    /**
     * Get total income
     */
    suspend fun getTotalIncome(): Double {
        val database = DatabaseProvider.getDatabase(context)
        return database.entryDao().getTotalIncome().first() ?: 0.0
    }

    /**
     * Get total expense
     */
    suspend fun getTotalExpense(): Double {
        val database = DatabaseProvider.getDatabase(context)
        return database.entryDao().getTotalExpense().first() ?: 0.0
    }

    private suspend fun addMockData() {
        val database = DatabaseProvider.getDatabase(context)
        val entryDao = database.entryDao()

        val mockEntries = listOf(
            Entry(
                amount = 150.0,
                categoryId = "1",
                note = "Weekly Shop",
                type = EntryType.EXPENSE,
                timestamp = getMockDate(27)
            ),
            Entry(
                amount = 50.0,
                categoryId = "2",
                note = "Bus Pass",
                type = EntryType.EXPENSE,
                timestamp = getMockDate(25)
            ),
            Entry(
                amount = 200.0,
                categoryId = "1",
                note = "Dinner Out",
                type = EntryType.EXPENSE,
                timestamp = getMockDate(20)
            ),
            Entry(
                amount = 5000.0,
                categoryId = "3",
                note = "Monthly Pay",
                type = EntryType.INCOME,
                timestamp = getMockDate(1)
            )
        )

        mockEntries.forEach { entryDao.insertEntry(it) }
        println("EntryRepository: Added ${mockEntries.size} mock entries")
    }

    private fun getMockDate(day: Int): Long {
        return Calendar.getInstance().apply {
            set(2026, Calendar.APRIL, day, 12, 0)
        }.timeInMillis
    }
}
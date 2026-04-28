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
import java.util.UUID

class EntryRepository private constructor(private val context: Context) {

    private val database = DatabaseProvider.getDatabase(context)
    private val entryDao = database.entryDao()

    private var currentUserId: String = ""
    private var currentJob: kotlinx.coroutines.Job? = null

    private val _entries: MutableStateFlow<List<Entry>> = MutableStateFlow(emptyList())
    val entries: StateFlow<List<Entry>> = _entries.asStateFlow()

    /**
     * Set the current logged-in user and load their data
     * Call this after successful login
     */
    fun setCurrentUser(userId: String) {
        if (currentUserId == userId) return

        // Cancel previous collection job
        currentJob?.cancel()

        currentUserId = userId

        // Start new collection for this user
        currentJob = CoroutineScope(Dispatchers.IO).launch {
            entryDao.getEntriesByUserId(userId)
                .catch { e -> e.printStackTrace() }
                .collect { entryList ->
                    _entries.value = entryList
                }
        }
    }

    fun clearCurrentUser() {
        currentJob?.cancel()
        currentUserId = ""
        _entries.value = emptyList()
    }

    fun getCurrentUserId(): String = currentUserId

    fun addEntry(
        amount: Double,
        categoryId: String,
        type: EntryType,
        note: String? = null,
        timestamp: Long = System.currentTimeMillis(),
        imageBitmap: Bitmap? = null
    ) {
        if (currentUserId.isEmpty()) {
            println("EntryRepository: Cannot add entry - no user logged in")
            return
        }

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
                    attachmentUri = imagePath,
                    userId = currentUserId
                )

                entryDao.insertEntry(entry)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addEntry(entry: Entry) {
        if (currentUserId.isEmpty()) {
            println("EntryRepository: Cannot add entry - no user logged in")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val entryWithUser = entry.copy(userId = currentUserId)
                entryDao.insertEntry(entryWithUser)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    companion object {
        @Volatile
        private var instance: EntryRepository? = null

        fun getInstance(context: Context): EntryRepository {
            return instance ?: synchronized(this) {
                instance ?: EntryRepository(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }
}
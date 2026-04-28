package com.CFC.pennywizeapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.CFC.pennywizeapp.models.Entry
import com.CFC.pennywizeapp.models.EntryType
import kotlinx.coroutines.flow.Flow

@Dao
interface EntryDao {

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertEntry(entry: Entry)

    @Insert(onConflict = OnConflictStrategy.Companion.REPLACE)
    suspend fun insertEntries(entries: List<Entry>)

    // Get entries for specific user only
    @Query("SELECT * FROM entries WHERE userId = :userId ORDER BY timestamp DESC")
    fun getEntriesByUserId(userId: String): Flow<List<Entry>>

    // Get entries by type for specific user
    @Query("SELECT * FROM entries WHERE userId = :userId AND type = :type ORDER BY timestamp DESC")
    fun getEntriesByTypeAndUserId(userId: String, type: EntryType): Flow<List<Entry>>

    // Get entries between dates for specific user
    @Query("SELECT * FROM entries WHERE userId = :userId AND timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getEntriesBetweenDatesByUserId(userId: String, startDate: Long, endDate: Long): Flow<List<Entry>>

    // Get entries for a specific date for a user
    @Query("SELECT * FROM entries WHERE userId = :userId AND date(timestamp/1000, 'unixepoch') = date(:date/1000, 'unixepoch') ORDER BY timestamp DESC")
    fun getEntriesByDateAndUserId(userId: String, date: Long): Flow<List<Entry>>

    // Get total income for a user
    @Query("SELECT SUM(amount) FROM entries WHERE userId = :userId AND type = 'INCOME'")
    fun getTotalIncomeByUserId(userId: String): Flow<Double?>

    // Get total expense for a user
    @Query("SELECT SUM(amount) FROM entries WHERE userId = :userId AND type = 'EXPENSE'")
    fun getTotalExpenseByUserId(userId: String): Flow<Double?>

    // Get entries by category for a user
    @Query("SELECT * FROM entries WHERE userId = :userId AND categoryId = :categoryId ORDER BY timestamp DESC")
    fun getEntriesByCategoryAndUserId(userId: String, categoryId: String): Flow<List<Entry>>

    // Delete entries for a specific user (when logging out)
    @Query("DELETE FROM entries WHERE userId = :userId")
    suspend fun deleteEntriesByUserId(userId: String)

    // Delete single entry by ID and userId (security: ensure user owns the entry)
    @Query("DELETE FROM entries WHERE id = :entryId AND userId = :userId")
    suspend fun deleteEntryByIdAndUserId(entryId: String, userId: String)
}
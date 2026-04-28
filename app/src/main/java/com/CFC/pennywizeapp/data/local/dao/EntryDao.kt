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

    @Query("SELECT * FROM entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE type = :type ORDER BY timestamp DESC")
    fun getEntriesByType(type: EntryType): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getEntriesBetweenDates(startDate: Long, endDate: Long): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE date(timestamp/1000, 'unixepoch') = date(:date/1000, 'unixepoch') ORDER BY timestamp DESC")
    fun getEntriesByDate(date: Long): Flow<List<Entry>>

    @Query("SELECT * FROM entries WHERE categoryId = :categoryId ORDER BY timestamp DESC")
    fun getEntriesByCategory(categoryId: String): Flow<List<Entry>>

    @Query("SELECT SUM(amount) FROM entries WHERE type = 'INCOME'")
    fun getTotalIncome(): Flow<Double?>

    @Query("SELECT SUM(amount) FROM entries WHERE type = 'EXPENSE'")
    fun getTotalExpense(): Flow<Double?>

    @Query("DELETE FROM entries WHERE id = :entryId")
    suspend fun deleteEntryById(entryId: String)

    @Query("DELETE FROM entries")
    suspend fun deleteAllEntries()
}
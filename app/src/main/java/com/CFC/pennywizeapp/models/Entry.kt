package com.CFC.pennywizeapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),

    val amount: Double,

    val categoryId: String, // Links to Category

    val note: String? = null,

    val timestamp: Long = System.currentTimeMillis(),

    val type: EntryType // INCOME or EXPENSE
)

enum class EntryType {
    INCOME,
    EXPENSE
}
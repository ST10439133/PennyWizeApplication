package com.CFC.pennywizeapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey
    val id: String = java.util.UUID.randomUUID().toString(),

    val name: String, // "Groceries", "Transport", "Salary"
    val type: CategoryType, // INCOME or EXPENSE
    val color: String, // Hex string "#FF5733"
    val icon: String // Icon name for UI (Material icon)
)

enum class CategoryType {
    INCOME,
    EXPENSE
}
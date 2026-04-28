package com.CFC.pennywizeapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class BudgetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,           // Links to Supabase user
    val minGoal: Double,
    val maxGoal: Double,
    val currentTotal: Double = 0.0
) {
    val progress: Float
        get() = if (maxGoal > 0) (currentTotal / maxGoal).toFloat().coerceIn(0f, 1f) else 0f

    val status: String
        get() = when {
            currentTotal < minGoal -> "Below Minimum"
            currentTotal <= maxGoal -> "Within Range"
            else -> "Above Maximum"
        }
}
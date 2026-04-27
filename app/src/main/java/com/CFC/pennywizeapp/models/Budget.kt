package com.CFC.pennywizeapp.models

data class Budget(
    val minGoal: Double = 0.0,
    val maxGoal: Double = 0.0,
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
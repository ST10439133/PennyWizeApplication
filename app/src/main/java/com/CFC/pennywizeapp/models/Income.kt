package com.CFC.pennywizeapp.models

data class Income(
    val id: String = java.util.UUID.randomUUID().toString(),
    val amount: Double,
    val source: String, // e.g., "Salary" or "Side Hustle"
    val timestamp: Long = System.currentTimeMillis()
)
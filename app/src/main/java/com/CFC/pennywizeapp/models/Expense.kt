package com.CFC.pennywizeapp.models

data class Expense(
    val id: String = java.util.UUID.randomUUID().toString(),
    val amount: Double,
    val category: String, // e.g., "Groceries"
    val reason: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val attachments: List<ReceiptAttachment> = emptyList() //
)

data class ReceiptAttachment(
    val id: String = java.util.UUID.randomUUID().toString(),
    val fileName: String, // e.g., "Receipt no. 1"
    val uri: String // File path for the image
)
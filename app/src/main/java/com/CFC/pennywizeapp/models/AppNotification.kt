package com.CFC.pennywizeapp.models

data class AppNotification(
    val id: String,
    val title: String, // "PennyWize"
    val message: String, // e.g., "New game challenge"
    val timeAgo: String, // e.g., "10 hrs ago"
    val type: NotificationCategory
)

enum class NotificationCategory {
    TRANSACTION, GAME, SYSTEM //
}


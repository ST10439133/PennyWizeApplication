package com.CFC.pennywizeapp.models

data class UserProfile(
    val name: String, //
    val email: String, //
    val phoneNumber: String, //
    val isSyncEnabled: Boolean // For "Synchronization and user status"
)
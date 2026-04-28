package com.CFC.pennywizeapp.models

data class UserProfile(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val isSyncEnabled: Boolean = true
)
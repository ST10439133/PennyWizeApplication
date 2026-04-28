package com.CFC.pennywizeapp.data

import com.CFC.pennywizeapp.models.UserProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.Serializable

class ProfileRepository(
    private val supabase: SupabaseClient
) {

    // Get current user's profile
    suspend fun getCurrentUserProfile(): UserProfile? {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return null

            val response = supabase.from("profiles")
                .select {
                    filter {
                        eq("id", currentUser.id)
                    }
                }

            // Decode the response
            val profiles = response.decodeList<com.CFC.pennywizeapp.data.ProfileResponse>()
            profiles.firstOrNull()?.toUserProfile()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Create or update user profile
    suspend fun upsertProfile(userProfile: UserProfile): Result<Unit> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(Exception("No user logged in"))

            val profileData = mapOf(
                "id" to currentUser.id,
                "name" to userProfile.name,
                "email" to userProfile.email,
                "phone_number" to userProfile.phoneNumber,
                "is_sync_enabled" to userProfile.isSyncEnabled,
                "updated_at" to System.currentTimeMillis().toString()
            )

            supabase.from("profiles")
                .upsert(profileData) {
                    onConflict = "id"
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Update specific fields
    suspend fun updateProfileField(
        field: String,
        value: Any
    ): Result<Unit> {
        return try {
            val currentUser = supabase.auth.currentUserOrNull()
                ?: return Result.failure(Exception("No user logged in"))

            supabase.from("profiles")
                .update(
                    mapOf(
                        field to value,
                        "updated_at" to System.currentTimeMillis().toString()
                    )
                ) {
                    filter {
                        eq("id", currentUser.id)
                    }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Observe profile changes in real-time
    fun observeProfile(): Flow<UserProfile?> = flow {
        while (true) {
            emit(getCurrentUserProfile())
            delay(1000) // Polling interval
            // In production, use Supabase Realtime subscriptions
        }
    }

    // Sync status update
    suspend fun setSyncEnabled(enabled: Boolean): Result<Unit> {
        return updateProfileField("is_sync_enabled", enabled)
    }

    // Update user name
    suspend fun updateName(name: String): Result<Unit> {
        return updateProfileField("name", name)
    }

    // Update phone number
    suspend fun updatePhoneNumber(phoneNumber: String): Result<Unit> {
        return updateProfileField("phone_number", phoneNumber)
    }
}

// Response mapper - must be Serializable for JSON parsing
@Serializable
private data class ProfileResponse(
    val id: String,
    val name: String? = null,
    val email: String? = null,
    val phone_number: String? = null,
    val is_sync_enabled: Boolean = true,
    val created_at: String? = null,
    val updated_at: String? = null
) {
    fun toUserProfile() = UserProfile(
        id = id,
        name = name ?: "",
        email = email ?: "",
        phoneNumber = phone_number ?: "",
        isSyncEnabled = is_sync_enabled,

        )
}

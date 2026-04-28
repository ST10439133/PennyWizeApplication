package com.CFC.pennywizeapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.CFC.pennywizeapp.SupabaseClient
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.auth

class AuthViewModel : ViewModel() {
    private val supabase = SupabaseClient.instance

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val user = supabase.auth.currentUserOrNull()
                _isAuthenticated.value = user != null
            } catch (e: Exception) {
                _isAuthenticated.value = false
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }
                _isAuthenticated.value = true
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Login failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signUp(email: String, password: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                supabase.auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                _message.value = "Account created! Check your email."
                onSuccess()
            } catch (e: Exception) {
                _error.value = e.message ?: "Sign up failed"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _isAuthenticated.value = false
                onSuccess()
            } catch (e: Exception) {
                _error.value = "Sign out failed: ${e.message}"
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
    fun clearMessage() {
        _message.value = null
    }
}
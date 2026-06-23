package com.example.fittrackpro.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface AuthRepository {
    val currentUserState: StateFlow<String?>
    val isUserLoggedIn: Boolean
    suspend fun login(email: String, password: String): Result<String>
    suspend fun register(email: String, password: String): Result<String>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    fun logout()
}

class DefaultAuthRepository @Inject constructor(
    private val context: Context,
    externalScope: CoroutineScope
) : AuthRepository {

    private val prefs: SharedPreferences = context.getSharedPreferences("fittrackpro_auth_prefs", Context.MODE_PRIVATE)

    private val _currentUserState = MutableStateFlow<String?>(prefs.getString("current_user_email", null))
    override val currentUserState: StateFlow<String?> = _currentUserState.asStateFlow()

    override val isUserLoggedIn: Boolean
        get() = prefs.getString("current_user_email", null) != null

    override suspend fun login(email: String, password: String): Result<String> = runCatching {
        if (email.isBlank() || password.isBlank()) {
            throw Exception("Email and password cannot be empty")
        }
        val registeredPassword = prefs.getString("user_pwd_${email.trim()}", null)
        if (registeredPassword == null) {
            // For simple offline testing, auto-register the user if they don't exist
            prefs.edit().putString("user_pwd_${email.trim()}", password).apply()
        } else if (registeredPassword != password) {
            throw Exception("Incorrect password")
        }
        prefs.edit().putString("current_user_email", email.trim()).apply()
        _currentUserState.value = email.trim()
        email.trim()
    }

    override suspend fun register(email: String, password: String): Result<String> = runCatching {
        if (email.isBlank() || password.isBlank()) {
            throw Exception("Email and password cannot be empty")
        }
        prefs.edit()
            .putString("user_pwd_${email.trim()}", password)
            .putString("current_user_email", email.trim())
            .apply()
        _currentUserState.value = email.trim()
        email.trim()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> = runCatching {
        if (email.isBlank()) {
            throw Exception("Email cannot be empty")
        }
    }

    override fun logout() {
        prefs.edit().remove("current_user_email").apply()
        _currentUserState.value = null
    }
}

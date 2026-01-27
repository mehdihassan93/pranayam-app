package com.pranayam.app.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pranayam.app.api.PranayamApiService
import com.pranayam.app.api.SendOtpRequest
import com.pranayam.app.api.VerifyOtpRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    object OtpSent : AuthState()
    data class Authenticated(val needsOnboarding: Boolean) : AuthState()
    data class Error(val message: String) : AuthState()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val apiService: PranayamApiService,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val prefs = context.getSharedPreferences("pranayam_auth", Context.MODE_PRIVATE)

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber: StateFlow<String> = _phoneNumber.asStateFlow()

    private val _otp = MutableStateFlow("")
    val otp: StateFlow<String> = _otp.asStateFlow()

    fun onPhoneNumberChange(value: String) {
        _phoneNumber.value = value
    }

    fun onOtpChange(value: String) {
        if (value.length <= 6) {
            _otp.value = value
        }
    }

    fun sendOtp() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val phone = formatPhoneNumber(_phoneNumber.value)
                val response = apiService.sendOtp(SendOtpRequest(phone))
                if (response.isSuccessful && response.body()?.type == "success") {
                    _authState.value = AuthState.OtpSent
                } else {
                    _authState.value = AuthState.Error(response.body()?.message ?: "Failed to send OTP")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Network error")
            }
        }
    }

    fun verifyOtp() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val phone = formatPhoneNumber(_phoneNumber.value)
                val response = apiService.verifyOtp(VerifyOtpRequest(phone, _otp.value))
                if (response.isSuccessful) {
                    val authResponse = response.body()
                    if (authResponse != null) {
                        // Save auth token
                        saveAuthToken(authResponse.access_token)
                        saveUserId(authResponse.user.id)

                        // Check if user needs onboarding (name is "New User" means new account)
                        val needsOnboarding = authResponse.user.name == "New User"
                        _authState.value = AuthState.Authenticated(needsOnboarding)
                    } else {
                        _authState.value = AuthState.Error("Invalid response")
                    }
                } else {
                    _authState.value = AuthState.Error("Invalid OTP")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Network error")
            }
        }
    }

    fun resetToPhoneEntry() {
        _authState.value = AuthState.Idle
        _otp.value = ""
    }

    fun clearError() {
        _authState.value = if (_otp.value.isNotEmpty()) AuthState.OtpSent else AuthState.Idle
    }

    private fun formatPhoneNumber(phone: String): String {
        val cleaned = phone.replace(Regex("[^0-9]"), "")
        return if (cleaned.startsWith("91")) "+$cleaned"
        else if (cleaned.length == 10) "+91$cleaned"
        else "+$cleaned"
    }

    private fun saveAuthToken(token: String) {
        prefs.edit().putString("auth_token", token).apply()
    }

    private fun saveUserId(userId: String) {
        prefs.edit().putString("user_id", userId).apply()
    }

    fun getAuthToken(): String? = prefs.getString("auth_token", null)
    fun getUserId(): String? = prefs.getString("user_id", null)
    fun isLoggedIn(): Boolean = getAuthToken() != null

    fun logout() {
        prefs.edit().clear().apply()
        _authState.value = AuthState.Idle
        _phoneNumber.value = ""
        _otp.value = ""
    }
}

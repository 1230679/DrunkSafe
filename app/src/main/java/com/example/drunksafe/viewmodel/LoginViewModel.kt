package com. example.drunksafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drunksafe.data.repositories.AuthRepository
import com.example.drunksafe.data.repositories.UserProfile
import com.example.drunksafe.data.repositories.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx. coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    object PasswordResetSent : AuthUiState()
    data class Success(val userId: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class LoginViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
    private val userRepo: UserRepository = UserRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState. Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val user = authRepo.signIn(email, password)
                if (user != null) _uiState.value = AuthUiState.Success(user. uid)
                else _uiState.value = AuthUiState.Error("User is null")
            } catch (e: Exception) {
                _uiState.value = AuthUiState. Error(e.message ?: "Login failed")
            }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val user = authRepo.signUp(email, password, displayName)
                if (user != null) {
                    // Criar perfil com setupCompleted = false
                    val profile = UserProfile(
                        uid = user.uid,
                        email = user.email ?: "",
                        displayName = displayName,
                        homeAddress = "",
                        homeLat = null,
                        homeLng = null,
                        setupCompleted = false,
                        dateOfBirth = "",
                        phoneCountryCode = "+351",
                        phoneNumber = ""
                    )
                    userRepo.saveUserProfile(profile)
                    _uiState.value = AuthUiState.Success(user. uid)
                } else {
                    _uiState.value = AuthUiState.Error("User is null after signUp")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState. Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun resetPassword(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                authRepo.sendPasswordReset(email)
                _uiState.value = AuthUiState.PasswordResetSent
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Failed to send reset email")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    fun signOut() {
        authRepo.signOut()
        _uiState.value = AuthUiState. Idle
    }
}
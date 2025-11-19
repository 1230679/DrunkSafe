package com.example.drunksafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drunksafe.data.AuthRepository
import com.example.drunksafe.data.UserProfile
import com.example.drunksafe.data.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val userId: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class LoginViewModel(
    private val authRepo: AuthRepository = AuthRepository(),
    private val userRepo: UserRepository = UserRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val user = authRepo.signIn(email, password)
                if (user != null) _uiState.value = AuthUiState.Success(user.uid)
                else _uiState.value = AuthUiState.Error("User is null")
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Login failed")
            }
        }
    }

    fun signUp(email: String, password: String, displayName: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            try {
                val user = authRepo.signUp(email, password, displayName)
                if (user != null) {
                    val profile = UserProfile(uid = user.uid, email = user.email ?: "", displayName = displayName)
                    userRepo.saveUserProfile(profile)
                    _uiState.value = AuthUiState.Success(user.uid)
                } else {
                    _uiState.value = AuthUiState.Error("User is null after signUp")
                }
            } catch (e: Exception) {
                _uiState.value = AuthUiState.Error(e.message ?: "Sign up failed")
            }
        }
    }

    fun signOut() {
        authRepo.signOut()
        _uiState.value = AuthUiState.Idle
    }
}
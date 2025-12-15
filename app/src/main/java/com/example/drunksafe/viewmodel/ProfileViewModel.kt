package com.example.drunksafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drunksafe.data.repositories.UserProfile
import com.example.drunksafe.data.repositories.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class ProfileUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val displayName: String = "",
    val dateOfBirth: String = "",
    val phoneCountryCode: String = "+351",
    val phoneNumber: String = ""
)

/**
 * ViewModel responsible for loading and updating the current user's profile.
 *
 * NOTE: dateOfBirth is not yet in UserProfile. We read/write it as a separate
 * Firestore field via UserRepository extension functions below.
 */
class ProfileViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState(isLoading = true))
    val uiState: StateFlow<ProfileUiState> = _uiState

    private val currentUid: String?
        get() = auth.currentUser?.uid

    init {
        loadProfile()
    }

    fun loadProfile() {
        val uid = currentUid ?: run {
            _uiState.value = ProfileUiState(
                isLoading = false,
                errorMessage = "User not authenticated."
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
            try {
                val profile = userRepository.getUserProfile(uid) // use this, not getUser    Profile
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    displayName = profile?.displayName ?: "",
                    dateOfBirth = profile?.dateOfBirth ?: "",
                    phoneCountryCode = profile?.phoneCountryCode ?: "+351",
                    phoneNumber = profile?.phoneNumber ?: ""
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load profile."
                )
            }
        }
    }

    fun onDisplayNameChange(newName: String) {
        _uiState.value = _uiState.value.copy(displayName = newName, successMessage = null)
    }

    fun onDateOfBirthChange(newDob: String) {
        _uiState.value = _uiState.value.copy(dateOfBirth = newDob, successMessage = null)
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }

    fun onPhoneCountryCodeChange(code: String) {
        _uiState.value = _uiState.value.copy(phoneCountryCode = code, successMessage = null)
    }

    fun onPhoneNumberChange(number: String) {
        // reuse your existing validator logic (digits only, length rules, etc.)
        _uiState.value = _uiState.value.copy(phoneNumber = number, successMessage = null)
    }

    fun saveProfile() {
        val uid = currentUid ?: run {
            _uiState.value = _uiState.value.copy(
                errorMessage = "User not authenticated."
            )
            return
        }

        val state = _uiState.value
        if (state.displayName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Name cannot be empty.")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                errorMessage = null,
                successMessage = null
            )
            try {
                // Load existing profile so we do not overwrite other fields
                val existing = userRepository.getUserProfile(uid)
                val updated = (existing ?: UserProfile(uid = uid)).copy(
                    displayName = state.displayName,
                    dateOfBirth = state.dateOfBirth,
                    phoneCountryCode = state.phoneCountryCode,
                    phoneNumber = state.phoneNumber
                )

                userRepository.saveUserProfile(updated)

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    successMessage = "Profile updated successfully."
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to update profile."
                )
            }
        }
    }
}
package com.example.drunksafe.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drunksafe.data.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class HomeAddressUiState(
    val isLoading: Boolean = false,
    val address: String = "",
    val error: String? = null
)

class HomeAddressViewModel(
    private val repo: UserRepository = UserRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow(HomeAddressUiState(isLoading = true))
    val state: StateFlow<HomeAddressUiState> = _state

    init {
        load()
    }

    fun load() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val profile = repo.getUserProfile(uid)
                _state.value = HomeAddressUiState(
                    isLoading = false,
                    address = profile?.homeAddress ?: ""
                )
            } catch (e: Exception) {
                _state.value = HomeAddressUiState(
                    isLoading = false,
                    error = "Failed to load address"
                )
            }
        }
    }

    fun onAddressChange(value: String) {
        _state.value = _state.value.copy(address = value, error = null)
    }

    fun save(lat: Double, lng: Double, onDone: () -> Unit) {
        val uid = auth.currentUser?.uid ?: return
        val address = _state.value.address

        if (address.isBlank()) {
            _state.value = _state.value.copy(error = "Address cannot be empty")
            return
        }

        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true)
                repo.updateHomeAddress(uid, address, lat, lng)
                _state.value = _state.value.copy(isLoading = false)
                onDone()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Failed to save address"
                )
            }
        }
    }
}
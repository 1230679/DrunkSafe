package com.example.drunksafe.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drunksafe.data.repositories.ContactsRepository
import com.example.drunksafe.data.repositories.UserRepository
import com.google.firebase.auth. FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines. launch

sealed class SetupState {
    object Idle : SetupState()
    object Loading : SetupState()
    object Checking : SetupState()
    object NeedsSetup : SetupState()
    object SetupComplete : SetupState()
    data class Error(val message: String) : SetupState()
}

class SetupViewModel(
    private val userRepository: UserRepository = UserRepository(),
    private val contactsRepository: ContactsRepository = ContactsRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    private val _state = MutableStateFlow<SetupState>(SetupState.Idle)
    val state: StateFlow<SetupState> = _state

    fun checkIfSetupNeeded() {
        viewModelScope.launch {
            _state.value = SetupState.Checking
            val uid = auth.currentUser?.uid
            if (uid == null) {
                _state.value = SetupState.NeedsSetup
                return@launch
            }

            val isCompleted = userRepository.isSetupCompleted(uid)
            _state. value = if (isCompleted) SetupState.SetupComplete else SetupState. NeedsSetup
        }
    }

    fun completeSetup(contacts: List<Pair<String, String>>, homeAddress: String) {
        viewModelScope. launch {
            _state.value = SetupState.Loading
            val uid = auth.currentUser?.uid
            if (uid == null) {
                _state.value = SetupState.Error("User not logged in")
                return@launch
            }

            try {
                // Adicionar os contactos (só se houver contactos válidos)
                val validContacts = contacts.filter { it. first. isNotBlank() && it. second.isNotBlank() }
                if (validContacts.isNotEmpty()) {
                    val contactsSuccess = contactsRepository.addMultipleContacts(validContacts)
                    Log.d("SetupViewModel", "Contacts saved: $contactsSuccess")
                }

                // Atualizar o perfil com a morada e marcar setup como completo
                userRepository.completeSetup(uid, homeAddress)
                Log.d("SetupViewModel", "Setup marked as complete for uid: $uid")

                _state.value = SetupState. SetupComplete
            } catch (e: Exception) {
                Log.e("SetupViewModel", "Setup failed: ${e. message}")
                _state.value = SetupState.Error(e.message ?: "Setup failed")
            }
        }
    }
}

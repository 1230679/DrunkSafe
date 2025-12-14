package com.example. drunksafe.viewmodel

import androidx. lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.drunksafe.data.repositories.ContactsRepository
import com.example.drunksafe.data.repositories.TrustedContact
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines. flow.asStateFlow
import kotlinx. coroutines.launch

data class TrustedContactsUiState(
    val contacts: List<TrustedContact> = emptyList(),
    val searchQuery: String = "",
    val isLoading: Boolean = false
)

class TrustedContactsViewModel(
    private val repository: ContactsRepository = ContactsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(TrustedContactsUiState(isLoading = true))
    val uiState: StateFlow<TrustedContactsUiState> = _uiState. asStateFlow()

    init {
        loadContacts()
    }

    private fun loadContacts() {
        viewModelScope.launch {
            repository.getContactsFlow().collect { contacts ->
                _uiState.value = _uiState.value.copy(
                    contacts = contacts,
                    isLoading = false
                )
            }
        }
    }

    fun updateSearchQuery(query: String) {
        _uiState. value = _uiState.value.copy(searchQuery = query)
    }

    fun getFilteredContacts(): List<TrustedContact> {
        val query = _uiState.value.searchQuery.lowercase()
        return if (query. isBlank()) {
            _uiState.value.contacts
        } else {
            _uiState.value.contacts.filter {
                it.name.lowercase().contains(query)
            }
        }
    }

    fun addContact(name: String, phoneNumber: String) {
        viewModelScope.launch {
            repository.addContact(name, phoneNumber)
        }
    }

    fun deleteContact(contactId: String) {
        viewModelScope.launch {
            repository.deleteContact(contactId)
        }
    }
}
package com. example.drunksafe.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx. coroutines.flow. asStateFlow

data class TrustedContact(
    val id: String = System.currentTimeMillis().toString(),
    val name: String = "",
    val phoneNumber: String = ""
)

data class TrustedContactsUiState(
    val contacts: List<TrustedContact> = emptyList(),
    val searchQuery: String = ""
)

class TrustedContactsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(
        TrustedContactsUiState(
            // Pre-populated sample contacts
            contacts = listOf(
                TrustedContact(id = "1", name = "Anna", phoneNumber = "+351 912 345 678"),
                TrustedContact(id = "2", name = "Jacob", phoneNumber = "+351 923 456 789"),
                TrustedContact(id = "3", name = "William", phoneNumber = "+351 934 567 890"),
                TrustedContact("4", "berna","+351962412837")
            )
        )
    )
    val uiState: StateFlow<TrustedContactsUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value. copy(searchQuery = query)
    }

    fun getFilteredContacts(): List<TrustedContact> {
        val query = _uiState.value.searchQuery.lowercase()
        return if (query.isBlank()) {
            _uiState.value. contacts
        } else {
            _uiState.value. contacts.filter {
                it.name.lowercase().contains(query)
            }
        }
    }

    fun addContact(name: String, phoneNumber: String) {
        val newContact = TrustedContact(
            id = System.currentTimeMillis().toString(),
            name = name,
            phoneNumber = phoneNumber
        )
        _uiState.value = _uiState. value.copy(
            contacts = _uiState.value. contacts + newContact
        )
    }

    fun deleteContact(contactId: String) {
        _uiState. value = _uiState.value.copy(
            contacts = _uiState. value.contacts.filter { it.id != contactId }
        )
    }

    fun notifyContact(contact: TrustedContact) {
        // TODO: Implement later
    }
}
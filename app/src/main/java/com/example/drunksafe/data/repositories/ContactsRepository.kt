package com.example.drunksafe.data.repositories

import com.google.firebase. auth.FirebaseAuth
import com.google. firebase.firestore. FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx. coroutines.flow.Flow
import kotlinx. coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

data class TrustedContact(
    val id: String = "",
    val name: String = "",
    val phoneNumber: String = "",
    val userId: String = ""
)

class ContactsRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore. getInstance(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    private val contactsCollection = firestore.collection("contacts")

    private fun getCurrentUserId(): String?  = auth.currentUser?. uid

    fun getContactsFlow(): Flow<List<TrustedContact>> = callbackFlow {
        val userId = getCurrentUserId()
        if (userId == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = contactsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val contacts = snapshot?.documents?.mapNotNull { doc ->
                    doc. toObject(TrustedContact::class. java)?.copy(id = doc. id)
                } ?: emptyList()

                trySend(contacts)
            }

        awaitClose { listener.remove() }
    }

    suspend fun addContact(name: String, phoneNumber: String): Boolean {
        val userId = getCurrentUserId() ?: return false

        val contact = hashMapOf(
            "name" to name,
            "phoneNumber" to phoneNumber,
            "userId" to userId
        )

        return try {
            contactsCollection.add(contact).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    // Adicionar múltiplos contactos de uma vez (para o setup inicial)
    suspend fun addMultipleContacts(contacts: List<Pair<String, String>>): Boolean {
        val userId = getCurrentUserId() ?: return false

        return try {
            val batch = firestore.batch()
            contacts.forEach { (name, phone) ->
                val docRef = contactsCollection. document()
                batch.set(docRef, hashMapOf(
                    "name" to name,
                    "phoneNumber" to phone,
                    "userId" to userId
                ))
            }
            batch. commit().await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteContact(contactId: String): Boolean {
        return try {
            contactsCollection.document(contactId).delete(). await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
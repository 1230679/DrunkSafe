package com.example. drunksafe.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val homeAddress: String = "",
    val setupCompleted: Boolean = false
)

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore. getInstance()
) {
    private val usersCollection = firestore.collection("users")

    suspend fun saveUserProfile(profile: UserProfile) {
        usersCollection.document(profile. uid).set(profile). await()
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        return try {
            val doc = usersCollection. document(uid).get().await()
            doc.toObject(UserProfile::class. java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun isSetupCompleted(uid: String): Boolean {
        return try {
            val doc = usersCollection. document(uid).get(). await()
            doc.getBoolean("setupCompleted") ?: false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun completeSetup(uid: String, homeAddress: String) {
        usersCollection.document(uid).update(
            mapOf(
                "homeAddress" to homeAddress,
                "setupCompleted" to true
            )
        ).await()
    }
}
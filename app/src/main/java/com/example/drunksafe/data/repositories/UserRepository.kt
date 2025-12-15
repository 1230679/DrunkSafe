package com.example.drunksafe.data.repositories

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.SetOptions


data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val homeAddress: String = "",
    val homeLat: Double? = null,
    val homeLng: Double? = null,
    val setupCompleted: Boolean = false,
    val dateOfBirth: String = "",
    val phoneCountryCode: String = "+351",
    val phoneNumber: String = ""
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

    suspend fun updateHomeAddress(
        uid: String,
        address: String,
        lat: Double,
        lng: Double
    ) {
        usersCollection.document(uid).set(
            mapOf(
                "homeAddress" to address,
                "homeLat" to lat,
                "homeLng" to lng
            ),
            SetOptions.merge()
        ).await()
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
        try {
            usersCollection.document(uid).set(
                mapOf(
                    "homeAddress" to homeAddress,
                    "setupCompleted" to true
                ),
                SetOptions. merge()
            ). await()
            Log.d("UserRepository", "completeSetup SUCCESS for $uid")
        } catch (e: Exception) {
            Log.e("UserRepository", "completeSetup FAILED: ${e.message}")
            throw e  // Re-throw para o ViewModel apanhar o erro
        }
    }
}
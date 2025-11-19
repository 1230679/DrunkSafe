package com.example.drunksafe.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val displayName: String? = null,
    val homeLat: Double? = null,
    val homeLng: Double? = null
)

class UserRepository(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    private val users = firestore.collection("users")

    suspend fun saveUserProfile(profile: UserProfile) {
        users.document(profile.uid).set(profile).await()
    }

    suspend fun getUserProfile(uid: String): UserProfile? {
        val doc = users.document(uid).get().await()
        return doc.toObject(UserProfile::class.java)
    }
}
package com.example.drunksafe.data.repositories

import android.content.Context
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    suspend fun signIn(email: String, password: String): FirebaseUser? {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user
    }

    suspend fun signUp(email: String, password: String, displayName: String? = null): FirebaseUser? {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val user = result.user
        if (user != null && !displayName.isNullOrBlank()) {
            val profile = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profile).await()
        }
        user?.sendEmailVerification()?.await()
        return user
    }

    suspend fun sendPasswordReset(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    fun signOut() {
        auth.signOut()
    }

    fun currentUser(): FirebaseUser? = auth.currentUser
}

class HomeAddressPreferences(context: Context) {
    private val prefs = context.getSharedPreferences("drunksafe_prefs", Context.MODE_PRIVATE)

    fun saveHomeAddress(address: String, lat: Double, lng: Double) {
        prefs.edit().apply {
            putString("HOME_ADDRESS", address)
            putFloat("HOME_LAT", lat.toFloat())
            putFloat("HOME_LNG", lng.toFloat())
            apply()
        }
    }

    fun getHomeAddress(): String? {
        return prefs.getString("HOME_ADDRESS", null)
    }

    fun getHomeLatLng(): LatLng? {
        val lat = prefs.getFloat("HOME_LAT", 0f)
        val lng = prefs.getFloat("HOME_LNG", 0f)

        if (lat == 0f && lng == 0f) return null

        return LatLng(lat.toDouble(), lng.toDouble())
    }
}
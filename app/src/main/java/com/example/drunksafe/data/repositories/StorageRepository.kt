package com.example.drunksafe.data.repositories

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class StorageRepository(
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
) {
    suspend fun uploadProfileImage(uid: String, fileUri: Uri): String {
        val ref = storage.reference.child("profile_images/$uid.jpg")
        ref.putFile(fileUri).await()
        return ref.downloadUrl.await().toString()
    }
}
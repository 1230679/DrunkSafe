package com.example.drunksafe.data.services

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.tasks.await

class LocationService(context: Context) {
    private val client = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): LatLng? {
        return try {
            val location: Location? = client.lastLocation.await()
            location?.let { LatLng(it.latitude, it.longitude) }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
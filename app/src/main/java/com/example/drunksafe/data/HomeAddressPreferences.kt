package com.example.drunksafe.data

import android.content.Context
import com.google.android.gms.maps.model.LatLng

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
package com.example.drunksafe.data.repositories

import android.util.Log
import com.example.drunksafe.data.DirectionsResponse
import com.example.drunksafe.data.services.RetrofitClient

class DirectionsRepository {

    private val api = RetrofitClient.apiService

    suspend fun getRoute(origin: String, destination: String, apiKey: String): DirectionsResponse? {
        return try {
            val response = api.getDirections(origin, destination, apiKey = apiKey)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()

                if (body?.status != "OK") {
                    Log.e("DirectionsRepo", "Error Google API: ${body?.status} - ${body?.errorMessage}")
                    return null
                }

                return body
            } else {
                Log.e("DirectionsRepo", "Error HTTP: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("DirectionsRepo", "Network Exception: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
package com.example.drunksafe.data

import android.util.Log

class DirectionsRepository {

    private val api = RetrofitClient.apiService

    suspend fun getRoute(origin: String, destination: String, apiKey: String): DirectionsResponse? {
        return try {
            val response = api.getDirections(origin, destination, apiKey = apiKey)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()

                if (body?.status != "OK") {
                    Log.e("DirectionsRepo", "Erro da Google API: ${body?.status} - ${body?.errorMessage}")
                    return null
                }

                return body
            } else {
                Log.e("DirectionsRepo", "Erro HTTP: ${response.code()} - ${response.message()}")
                null
            }
        } catch (e: Exception) {
            Log.e("DirectionsRepo", "Exceção de Rede: ${e.message}")
            e.printStackTrace()
            null
        }
    }
}
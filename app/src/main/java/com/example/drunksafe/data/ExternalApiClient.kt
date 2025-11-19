package com.example.drunksafe.data

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

object ExternalApiClient {
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }

    suspend fun getSafeRoute(lat: Double, lng: Double): String {
        val url = "https://example.com/api/safe-route?lat=$lat&lng=$lng"
        val response: HttpResponse = client.get(url)
        return response.bodyAsText()
    }
}
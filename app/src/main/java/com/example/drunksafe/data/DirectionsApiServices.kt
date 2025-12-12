package com.example.drunksafe.data

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query


interface DirectionsApiService {
    @GET("maps/api/directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String = "walking",
        @Query("key") apiKey: String
    ): Response<DirectionsResponse>
}

// 2. O Singleton do Retrofit (Cria a conexão apenas uma vez)
object RetrofitClient {
    private const val BASE_URL = "https://maps.googleapis.com/"

    val apiService: DirectionsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionsApiService::class.java)
    }
}
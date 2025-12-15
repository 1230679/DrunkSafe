package com.example.drunksafe.viewmodel

import android.app.Application
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.drunksafe.BuildConfig
import com.example.drunksafe.data.repositories.DirectionsRepository
import com.example.drunksafe.data.repositories.HomeAddressPreferences
import com.example.drunksafe.data.services.LocationService
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapViewModel(application: Application) : AndroidViewModel(application) {

    // Dependencies
    private val locationService = LocationService(application.applicationContext)
    private val repository = DirectionsRepository()
    private val prefs = HomeAddressPreferences(application.applicationContext)
    private val googleApiKey = BuildConfig.MAPS_API_KEY

    // --- STATE VARIABLES ---

    // Permission Status

    var hasLocationPermission by mutableStateOf(false)
        private set

    // Navigation Status
    var isNavigationMode by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    // Route Data
    var routePoints by mutableStateOf<List<LatLng>>(emptyList())
        private set

    var displayDistance by mutableStateOf("")
        private set

    var displayDuration by mutableStateOf("")
        private set

    // User Data
    var homeLocation by mutableStateOf<LatLng?>(null)
        private set

    // --- INIT ---

    init {
        loadHomeLocation()
    }

    // --- ACTIONS ---

    fun updatePermissionStatus(isGranted: Boolean) {
        hasLocationPermission = isGranted
    }

    fun loadHomeLocation() {
        val savedHome = prefs.getHomeLatLng()
        if (savedHome != null) {
            homeLocation = savedHome
        }
    }

    /**
     * Entry point for the Search Bar.
     * 1. Gets current location internally.
     * 2. Geocodes the text query.
     * 3. Starts navigation.
     */
    fun searchAndNavigateToPlace(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            isLoading = true

            // 1. Get Current Location
            val currentLocation = locationService.getCurrentLocation()
            if (currentLocation == null) {
                showToast("Unable to get current location. Check GPS.")
                isLoading = false
                return@launch
            }

            // 2. Search for the address (Geocoding)
            try {
                // Move Geocoding to IO thread
                val destinationLatLng = withContext(Dispatchers.IO) {
                    val geocoder = Geocoder(getApplication(), Locale.getDefault())
                    val results = geocoder.getFromLocationName(query, 1)
                    if (!results.isNullOrEmpty()) {
                        LatLng(results[0].latitude, results[0].longitude)
                    } else {
                        null
                    }
                }

                // 3. Handle Result
                if (destinationLatLng != null) {
                    calculateRoute(currentLocation, destinationLatLng)
                } else {
                    showToast("Location not found.")
                    isLoading = false
                }

            } catch (e: Exception) {
                showToast("Search error: ${e.localizedMessage}")
                isLoading = false
            }
        }
    }

    /**
     * Entry point for "Take Me Home" button.
     */
    fun startNavigationToHome() {
        viewModelScope.launch {
            val targetHome = prefs.getHomeLatLng()

            if (targetHome == null) {
                showToast("Define your home address first on your profile!")
                return@launch
            }

            isLoading = true

            // Get Current Location
            val currentLocation = locationService.getCurrentLocation()
            if (currentLocation == null) {
                showToast("Unable to get current location. Check GPS.")
                isLoading = false
                return@launch
            }

            // Start Navigation
            calculateRoute(currentLocation, targetHome)
        }
    }

    /**
     * Internal logic to calculate the route API request.
     */
    private suspend fun calculateRoute(origin: LatLng, destination: LatLng) {
        homeLocation = destination

        val originString = String.format(Locale.US, "%.6f,%.6f", origin.latitude, origin.longitude)
        val destString = String.format(Locale.US, "%.6f,%.6f", destination.latitude, destination.longitude)

        try {
            // Fetch Route from Repository
            val response = repository.getRoute(originString, destString, googleApiKey)

            if (response != null && response.routes.isNotEmpty()) {
                val route = response.routes[0]

                // Decode Polyline
                routePoints = PolyUtil.decode(route.overviewPolyline.points)

                // Update Distance/Duration
                if (route.legs.isNotEmpty()) {
                    displayDistance = route.legs[0].distance.text
                    displayDuration = route.legs[0].duration.text
                }

                isNavigationMode = true
            } else {
                showToast("No walking route found.")
            }
        } catch (e: Exception) {
            showToast("Network error: ${e.localizedMessage}")
        } finally {
            isLoading = false
        }
    }

    fun cancelNavigation() {
        isNavigationMode = false
        routePoints = emptyList()
        displayDistance = ""
        displayDuration = ""
        loadHomeLocation()
    }

    private fun showToast(message: String) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show()
    }
}
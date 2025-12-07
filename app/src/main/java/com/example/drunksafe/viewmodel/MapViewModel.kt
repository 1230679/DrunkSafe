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
import com.example.drunksafe.data.DirectionsRepository
import com.example.drunksafe.data.HomeAddressPreferences
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class MapViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = DirectionsRepository()
    private val googleApiKey = BuildConfig.MAPS_API_KEY
    private val prefs = HomeAddressPreferences(application.applicationContext)

    var isNavigationMode by mutableStateOf(false)
        private set

    var isLoading by mutableStateOf(false)
        private set

    var routePoints by mutableStateOf<List<LatLng>>(emptyList())
        private set

    var displayDistance by mutableStateOf("")
        private set

    var displayDuration by mutableStateOf("")
        private set

    var homeLocation by mutableStateOf<LatLng?>(null)
        private set

    init {
        loadHomeLocation()
    }

    fun loadHomeLocation() {
        val savedHome = prefs.getHomeLatLng()
        if (savedHome != null) {
            homeLocation = savedHome
        }
    }

    fun searchAndNavigate(query: String, currentLocation: LatLng) {
        if (query.isBlank()) return

        isLoading = true

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(getApplication(), Locale.getDefault())
                val results = geocoder.getFromLocationName(query, 1)

                if (!results.isNullOrEmpty()) {
                    val location = results[0]
                    val destinationLatLng = LatLng(location.latitude, location.longitude)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(getApplication(), "Going to: ${location.featureName ?: query}", Toast.LENGTH_SHORT).show()
                        startNavigation(currentLocation, customDestination = destinationLatLng)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(getApplication(), "Location not found.", Toast.LENGTH_SHORT).show()
                        isLoading = false
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(getApplication(), "Search error: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    isLoading = false
                }
            }
        }
    }

    fun startNavigation(currentLocation: LatLng, customDestination: LatLng? = null) {
        viewModelScope.launch {
            val targetDestination = customDestination ?: prefs.getHomeLatLng()

            if (targetDestination == null) {
                Toast.makeText(getApplication(), "Define your home address first on your profile!", Toast.LENGTH_LONG).show()
                return@launch
            }

            if (customDestination == null) {
                homeLocation = targetDestination
            } else {
                homeLocation = targetDestination
            }

            isLoading = true

            val originString = String.format(Locale.US, "%.6f,%.6f", currentLocation.latitude, currentLocation.longitude)
            val destString = String.format(Locale.US, "%.6f,%.6f", targetDestination.latitude, targetDestination.longitude)

            val response = repository.getRoute(originString, destString, googleApiKey)

            if (response != null && response.routes.isNotEmpty()) {
                val route = response.routes[0]
                routePoints = PolyUtil.decode(route.overviewPolyline.points)

                if (route.legs.isNotEmpty()) {
                    displayDistance = route.legs[0].distance.text
                    displayDuration = route.legs[0].duration.text
                }
                isNavigationMode = true
            } else {
                Toast.makeText(getApplication(), "There's no way to walk to the address.", Toast.LENGTH_SHORT).show()
            }
            isLoading = false
        }
    }

    fun cancelNavigation() {
        isNavigationMode = false
        routePoints = emptyList()
        displayDistance = ""
        displayDuration = ""
    }
}
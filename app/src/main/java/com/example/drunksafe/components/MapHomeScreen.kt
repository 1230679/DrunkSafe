package com.example.drunksafe.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drunksafe.R
import com.example.drunksafe.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.example.drunksafe.ui.theme.DarkBlue
import com.example.drunksafe.ui.theme.ErrorRed
import com.example.drunksafe.ui.theme.GoldYellow
import com.example.drunksafe.ui.theme.RouteBlue
import com.example.drunksafe.ui.theme.White


/**
 * The Main Dashboard Screen of the application.
 *
 * This screen orchestrates the Google Maps experience and switches between three distinct states:
 * 1. **Permission Denied:** Shows a prompt to enable location services.
 * 2. **Search Mode (Default):** Displays the map, a search bar, the "Take Me Home" button, and the bottom navigation.
 * 3. **Navigation Mode:** Displays the active route, distance/duration info, and a Stop button.
 *
 * It utilizes [GoogleMapBackground] for the map rendering and overlays UI components using a [Box] layout.
 *
 * @param onEmergencyAlertClick Callback for the bottom nav Emergency button.
 * @param onCallTrustedContactsClick Callback for the bottom nav Contacts button.
 * @param onSettingsClick Callback for the bottom nav settings button.
 * @param viewModel The state holder for map logic and location data.
 */
@Composable
fun MapHomeScreen(
    onEmergencyAlertClick: () -> Unit,
    onCallTrustedContactsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    // Permission Launcher: Handles the result of the system permission dialog
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            viewModel.updatePermissionStatus(isGranted)
        }
    )

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                viewModel.updatePermissionStatus(isGranted)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Initial Permission Check: Runs once when the screen loads
    LaunchedEffect(Unit) {
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. MAP LAYER (Background)
        GoogleMapBackground(
            isRouteActive = viewModel.isNavigationMode,
            routePoints = viewModel.routePoints,
            homeLocation = viewModel.homeLocation ?: LatLng(56.1572, 10.2107),
            hasPermission = viewModel.hasLocationPermission
        )

        // 2. UI LAYER
        if (viewModel.hasLocationPermission) {
            // Permission Granted: Show normal UI

            if (viewModel.isNavigationMode) {
                // STATE A: Navigation Mode (Stop button + Info Card)
                NavigationModeUI(
                    viewModel = viewModel,
                    onStopClick = { viewModel.cancelNavigation() }
                )
            } else {
                // STATE B: Search Mode (Search Bar + Take Me Home + Bottom Nav)
                SearchModeUI(
                    viewModel = viewModel,
                    onEmergencyAlertClick = onEmergencyAlertClick,
                    onCallTrustedContactsClick = onCallTrustedContactsClick,
                    onSettingsClick = onSettingsClick,
                )
            }
        }
    }
}

/**
 * The Default UI overlay when the user is idle (not navigating).
 * Contains the Search Bar, the primary "Take Me Home" CTA, and the Bottom Navigation Bar.
 */
@Composable
fun SearchModeUI(
    viewModel: MapViewModel,
    onEmergencyAlertClick: () -> Unit,
    onCallTrustedContactsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {

        // --- Search Bar ---
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Search place...", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, "Search", tint = Color.Gray) },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(
                onSearch = {
                    keyboardController?.hide()
                    // Delegate logic to ViewModel. UI does not handle location.
                    viewModel.searchAndNavigateToPlace(searchText)
                }
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 40.dp, start = 12.dp, end = 12.dp)
                .align(Alignment.TopCenter)
                .background(Color.White, RoundedCornerShape(25.dp)),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = DarkBlue
            ),
            singleLine = true
        )

        // --- "TAKE ME HOME" Button ---
        Button(
            onClick = {
                // Delegate logic to ViewModel. It handles GPS internally.
                viewModel.startNavigationToHome()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-110).dp), // Positioned above bottom bar
            colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.elevation(12.dp)
        ) {
            if (viewModel.isLoading) {
                CircularProgressIndicator(color = DarkBlue, modifier = Modifier.size(24.dp))
            } else {
                Text(
                    text = "TAKE ME HOME",
                    style = androidx.compose.ui.text.TextStyle(
                        color = DarkBlue,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 3.sp
                    )
                )
            }
        }

        // --- Bottom Navigation Bar ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .background(White)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Helper to create bottom bar items
            BottomBarItem(iconRes = R.drawable.icon_emergency, onClick = onEmergencyAlertClick)
            BottomBarItem(iconRes = R.drawable.icon_contactos, onClick = onCallTrustedContactsClick)
            BottomBarItem(iconRes = R.drawable.icon_perfil, onClick = onSettingsClick)
        }
    }
}

/**
 * The Active UI overlay when a route is calculated.
 * Shows trip details and a button to launch external turn-by-turn navigation.
 */
@Composable
fun NavigationModeUI(
    viewModel: MapViewModel,
    onStopClick: () -> Unit
) {
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        // Stop Button
        Button(
            onClick = onStopClick,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 40.dp, start = 16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = White),
            shape = RoundedCornerShape(50)
        ) {
            Text("STOP", color = Color.Black, fontWeight = FontWeight.Bold)
        }

        // Navigation Info Card
        NavigationInfoCard(
            distance = viewModel.displayDistance,
            duration = viewModel.displayDuration,
            modifier = Modifier.align(Alignment.BottomCenter),
            onStartNavigation = {
                val destination = viewModel.homeLocation
                if (destination != null) {
                    // Launch external Google Maps app for turn-by-turn navigation
                    val uri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}&mode=w")
                    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                    mapIntent.setPackage("com.google.android.apps.maps")

                    try {
                        context.startActivity(mapIntent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "Google Maps is not installed!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error: Unknown destination", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
}

/**
 * Fallback UI shown when Location Permission is denied.
 */
@Composable
fun PermissionDeniedUI(
    onGrantClick: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            //the user needs to make a decision, that's why this is empty
        },
        title = {
            Text(
                text = "Permission Required",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = DarkBlue
            )
        },
        text = {
            Text(
                text = "You cannot use DrunkSafe without location access.\n\n" +
                        "This app relies on your GPS to guide you home safely. " +
                        "Please grant permission to continue.",
                fontSize = 16.sp,
                color = Color.Gray
            )
        },
        confirmButton = {
            Button(
                onClick = onGrantClick,
                colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("TRY AGAIN", color = DarkBlue, fontWeight = FontWeight.Bold)
            }
        },
        backgroundColor = Color.White,
        shape = RoundedCornerShape(16.dp)
    )
}

/**
 * A wrapper around the Google Maps Compose SDK.
 * Handles camera updates and drawing the route polyline.
 *
 * @param hasPermission Controls the `isMyLocationEnabled` property to prevent crashes.
 */
@Composable
fun GoogleMapBackground(
    isRouteActive: Boolean,
    routePoints: List<LatLng>,
    homeLocation: LatLng,
    hasPermission: Boolean,
    modifier: Modifier = Modifier
) {
    // Default camera position (can be adjusted)
    val defaultLocation = LatLng(56.1572, 10.2107)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 14f)
    }

    // Animate camera when route changes
    LaunchedEffect(isRouteActive, routePoints) {
        if (isRouteActive && routePoints.isNotEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(routePoints.first(), 15f)
            )
        }
    }

    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false, // We use our own buttons
            compassEnabled = false
        ),
        properties = MapProperties(
            // Enables the blue dot if permission is granted
            isMyLocationEnabled = hasPermission
        )
    ) {
        if (isRouteActive && routePoints.isNotEmpty()) {
            Polyline(
                points = routePoints,
                color = RouteBlue,
                width = 15f
            )
            Marker(
                state = MarkerState(position = routePoints.last()),
                title = "Home",
                snippet = "Destination"
            )
        }
    }
}

/**
 * A detail card shown during navigation, displaying distance, duration, and the Start Navigation button.
 */
@Composable
fun NavigationInfoCard (
    distance: String,
    duration: String,
    modifier: Modifier = Modifier,
    onStartNavigation: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DirectionsWalk,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = DarkBlue
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = distance, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
            Spacer(modifier = Modifier.width(24.dp))
            Text(text = duration, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = DarkBlue)
        }

        Text(
            text = "SAFE ROUTE",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = DarkBlue,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onStartNavigation,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "START",
                style = androidx.compose.ui.text.TextStyle(
                    color = DarkBlue,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp
                )
            )
        }
    }
}

/**
 * Helper component for consistent Bottom Navigation items.
 */
@Composable
fun RowScope.BottomBarItem(iconRes: Int, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.padding(6.dp)
        )
    }
}
package com.example.drunksafe.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drunksafe.R
import com.example.drunksafe.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.google.android.gms.location.LocationServices
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.LocalSoftwareKeyboardController

private val GoldYellow = Color(0xFFD4A84B)
private val DarkBlue = Color(0xFF0A1929)
private val White = Color(0xFFFFFFFF)
private val RouteBlue = Color(0xFF4285F4)

@Composable
fun GoogleMapBackground(
    isRouteActive: Boolean,
    routePoints: List<LatLng>,
    homeLocation: LatLng,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted -> hasLocationPermission = isGranted }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val defaultLocation = LatLng(56.1572, 10.2107)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(defaultLocation, 14f)
    }

    LaunchedEffect(isRouteActive, routePoints) {
        if (isRouteActive && routePoints.isNotEmpty()) {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(routePoints.first(), 15f)
            )
        }
    }

    // 4. Mapa
    GoogleMap(
        modifier = modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false,
            compassEnabled = false
        ),
        properties = MapProperties(
            isMyLocationEnabled = hasLocationPermission
        )
    ) {
        if (isRouteActive && routePoints.isNotEmpty()) {

            // A. Desenha a linha
            Polyline(
                points = routePoints,
                color = RouteBlue,
                width = 15f
            )

            // B. Desenha o pino no fim da linha
            Marker(
                state = MarkerState(position = routePoints.last()),
                title = "My home address",
                snippet = "Destination"
            )
        }
    }
}
@SuppressLint("MissingPermission")
@Composable
fun MapHomeScreen(
    onTakeMeHomeClick: () -> Unit,
    onEmergencyAlertClick: () -> Unit,
    onCallTrustedContactsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearch: (String) -> Unit,
    viewModel: MapViewModel = viewModel ()
) {
    val context = LocalContext.current

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val homeLocation = LatLng(56.1710, 10.2160)

    var searchText by remember { mutableStateOf("") }

    val keyboardController = LocalSoftwareKeyboardController.current

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMapBackground(
            isRouteActive = viewModel.isNavigationMode,
            routePoints = viewModel.routePoints,
            homeLocation = homeLocation
        )

        // --- MODO 1: TELA NORMAL ---
        if (!viewModel.isNavigationMode) {

            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                placeholder = { Text("Search place...", color = Color.Gray) },
                leadingIcon = { Icon(Icons.Default.Search, "Search", tint = Color.Gray) },

                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),

                keyboardActions = KeyboardActions(
                    onSearch = {
                        keyboardController?.hide()

                        try {
                            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                if (location != null) {
                                    val currentLatLng = LatLng(location.latitude, location.longitude)

                                    viewModel.searchAndNavigate(searchText, currentLatLng)

                                } else {
                                    viewModel.searchAndNavigate(searchText, LatLng(56.1572, 10.2107))
                                }
                            }
                        } catch (e: SecurityException) {
                            e.printStackTrace()
                        }
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

            // Botão "TAKE ME HOME"
            Button(
                onClick = {
                    try {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                            if (location != null) {
                                val currentLatLng = LatLng(location.latitude, location.longitude)
                                viewModel.startNavigation(currentLatLng)
                            } else {
                                viewModel.startNavigation(LatLng(56.1572, 10.2107))                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 24.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-110).dp),
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

            // Bottom Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(White)
                    .align(Alignment.BottomCenter),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onEmergencyAlertClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_emergency), // Confirma se tens este recurso
                        contentDescription = "Emergency Alert",
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onCallTrustedContactsClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_contactos), // Confirma se tens este recurso
                        contentDescription = "Trusted Contacts",
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(6.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clickable(onClick = onProfileClick),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_perfil), // Confirma se tens este recurso
                        contentDescription = "Profile",
                        tint = Color.Unspecified,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        }

        // --- MODO 2: TELA DE NAVEGAÇÃO ---
        if (viewModel.isNavigationMode) {

            // Botão de Cancelar
            Button(
                onClick = { viewModel.cancelNavigation() },
                modifier = Modifier.align(Alignment.TopStart).padding(top = 40.dp, start = 16.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = White),
                shape = RoundedCornerShape(50)
            ) {
                Text("X Cancelar", color = Color.Black)
            }

            NavigationInfoCard(
                distance = viewModel.displayDistance,
                duration = viewModel.displayDuration,
                modifier = Modifier.align(Alignment.BottomCenter),
                onStartNavigation = {
                    val destination = viewModel.homeLocation

                    if (destination != null) {
                        val uri = Uri.parse("google.navigation:q=${destination.latitude},${destination.longitude}&mode=w")

                        val mapIntent = Intent(Intent.ACTION_VIEW, uri)

                        mapIntent.setPackage("com.google.android.apps.maps")

                        try {
                            context.startActivity(mapIntent)
                        } catch (e: Exception) {
                            Toast.makeText(context, "Google Maps não instalado!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Erro: Destino desconhecido", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}

@Composable
fun NavigationInfoCard(
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
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = distance,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = duration,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "SAFE",
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            color = DarkBlue,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Button(
            onClick = onStartNavigation,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
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
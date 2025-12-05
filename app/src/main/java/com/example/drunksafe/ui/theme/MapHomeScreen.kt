package com.example.drunksafe.ui

import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp // lógica de navegação
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.Search
import com.example.drunksafe.R

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private val GoldYellow = Color(0xFFD4A84B)
private val DarkBlue = Color(0xFF0A1929)
private val AlertRed = Color(0xFFD94A4A)

@Composable
fun GoogleMapBackground() {
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
        onResult = { isGranted ->
            hasLocationPermission = isGranted
        }
    )

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    val trojborgAarhus = LatLng(56.1678, 10.2081)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(trojborgAarhus, 14f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        uiSettings = MapUiSettings(
            zoomControlsEnabled = false,
            myLocationButtonEnabled = false
        ),
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission)
    ) {
    }
}


@Composable
fun MapHomeScreen(
    onTakeMeHomeClick: () -> Unit,
    onEmergencyAlertClick: () -> Unit,
    onCallTrustedContactsClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearch: (String) -> Unit
) {
    var searchText by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMapBackground()

        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it; /* Adicione lógica de pesquisa aqui */ onSearch(it) },
            placeholder = { Text("Search...", color = Color.Gray) },
            leadingIcon = {
                Icon(
                    Icons.Default.Search,
                    contentDescription = "Search Icon",
                    tint = Color.Gray
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, start = 12.dp, end = 12.dp)
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


        // 3. Botão "TAKE ME HOME" (Flutuante no centro-inferior)
        Button(
            onClick = onTakeMeHomeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 16.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-64).dp - 16.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.elevation(defaultElevation = 8.dp)
        ) {
            Text(
                "TAKE ME HOME",
                color = DarkBlue,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }


        // 4. Barra de Navegação Inferior (Bottom Bar)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .background(DarkBlue)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onEmergencyAlertClick) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_emergency),
                    contentDescription = "Emergency Alert",
                    tint = AlertRed,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(onClick = onCallTrustedContactsClick) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_contactos),
                    contentDescription = "Trusted Contacts",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }

            IconButton(onClick = onProfileClick) {
                Icon(
                    painter = painterResource(id = R.drawable.icon_perfil),
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
    }
}
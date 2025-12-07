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
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private val GoldYellow = Color(0xFFD4A84B)
private val DarkBlue = Color(0xFF0A1929)
private val White = Color(0xFFFFFFFF)


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


        // 3. Botão "TAKE ME HOME"
        Button(
            onClick = onTakeMeHomeClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 24.dp)
                .align(Alignment.BottomCenter)
                .offset(y = (-110).dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
            shape = RoundedCornerShape(16.dp),
            elevation = ButtonDefaults.elevation(
                defaultElevation = 12.dp,
                pressedElevation = 6.dp
            )
        ) {
            Text(
                text = "TAKE ME HOME",
                style = androidx.compose.ui.text.TextStyle(
                    color = DarkBlue,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 3.sp,
                    shadow = androidx.compose.ui.graphics.Shadow(
                        color = Color.Black.copy(alpha = 0.2f),
                        offset = androidx.compose.ui.geometry.Offset(2f, 2f),
                        blurRadius = 4f
                    )
                )
            )
        }


        // 4. Barra de Navegação Inferior (Bottom Bar)
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
                    painter = painterResource(id = R.drawable.icon_emergency),
                    contentDescription = "Emergency Alert",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
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
                    painter = painterResource(id = R.drawable.icon_contactos),
                    contentDescription = "Trusted Contacts",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
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
                    painter = painterResource(id = R.drawable.icon_perfil),
                    contentDescription = "Profile",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(6.dp)
                )
            }
        }
    }
}
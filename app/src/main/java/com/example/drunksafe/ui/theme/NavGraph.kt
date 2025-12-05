package com.example.drunksafe.ui

import androidx.compose. foundation.layout.Box
import androidx.compose. foundation.layout.fillMaxSize
import androidx.compose.material. CircularProgressIndicator
import androidx.compose. material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.drunksafe.viewmodel.LoginViewModel
import com. example.drunksafe.viewmodel. SetupState
import com.example.drunksafe.viewmodel.SetupViewModel
import com.example.drunksafe.viewmodel.TrustedContactsViewModel
import com.example.drunksafe.ui.MapHomeScreen

private val DarkBackground = Color(0xFF072E3A)
private val GoldAccent = Color(0xFFD8A84A)

@Composable
fun AppNavHost(onLoggedIn: (String) -> Unit) {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val setupViewModel: SetupViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { uid ->
                    onLoggedIn(uid)
                    navController.navigate("checkSetup") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpRequested = { navController. navigate("signup") },
                viewModel = loginViewModel
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpDone = { uid ->
                    onLoggedIn(uid)
                    navController.navigate("setup") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() },
                viewModel = loginViewModel
            )
        }

        composable("checkSetup") {
            val state by setupViewModel.state. collectAsState()

            LaunchedEffect(Unit) {
                setupViewModel.checkIfSetupNeeded()
            }

            LaunchedEffect(state) {
                when (state) {
                    is SetupState.SetupComplete -> {
                        navController.navigate("dashboard") {
                            popUpTo("checkSetup") { inclusive = true }
                        }
                    }
                    is SetupState. NeedsSetup -> {
                        navController.navigate("setup") {
                            popUpTo("checkSetup") { inclusive = true }
                        }
                    }
                    else -> {}
                }
            }

            // Loading enquanto verifica
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = DarkBackground
            ) {
                Box(contentAlignment = Alignment. Center) {
                    CircularProgressIndicator(color = GoldAccent)
                }
            }
        }

        composable("setup") {
            SetupScreen(
                onSetupComplete = { contacts, address ->
                    val contactPairs = contacts.map { it.name to it.phone }
                    setupViewModel.completeSetup(contactPairs, address)
                    navController.navigate("dashboard") {
                        popUpTo("setup") { inclusive = true }
                    }
                },
                onSkipSetup = {
                    // Apenas navega para home sem guardar nada
                    // O utilizador pode fazer o setup mais tarde
                    navController.navigate("dashboard") {
                        popUpTo("setup") { inclusive = true }
                    }
                }
            )
        }

    composable("dashboard") {
        val contactsViewModel: TrustedContactsViewModel = viewModel()

        MapHomeScreen(
            onTakeMeHomeClick = {
                // Navega para a rota de "em viagem"
                navController.navigate("route_in_progress")
            },
            onEmergencyAlertClick = {
                navController.navigate("emergency")
            },
            onCallTrustedContactsClick = {
                navController.navigate("trustedContacts")
            },
            onProfileClick = {
                navController.navigate("profile")
            },
            onSearch = { query ->
                // Lógica temporária para veres a funcionar no Logcat
                println("O utilizador pesquisou por: $query")
            }
        )
    }


        composable("trustedContacts") {
            val contactsViewModel: TrustedContactsViewModel = viewModel()
            TrustedContactsScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = contactsViewModel
            )
        }

       // composable("navigation") { }

        composable("emergency") {
            val contactsViewModel: TrustedContactsViewModel = viewModel()
            EmergencyScreen(
                onNavigateBack = { navController. popBackStack() },
                contactsViewModel = contactsViewModel
            )
        }
    }
}
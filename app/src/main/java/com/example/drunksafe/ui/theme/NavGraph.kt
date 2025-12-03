package com.example. drunksafe.ui

import androidx.compose. foundation.layout. Box
import androidx.compose.foundation.layout. fillMaxSize
import androidx.compose.material. CircularProgressIndicator
import androidx.compose. material.Surface
import androidx.compose.runtime.*
import androidx. compose.ui. Alignment
import androidx. compose.ui. Modifier
import androidx. compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx. navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.drunksafe.viewmodel.LoginViewModel
import com. example.drunksafe.viewmodel. SetupState
import com.example.drunksafe.viewmodel.SetupViewModel
import com.example.drunksafe.viewmodel.TrustedContactsViewModel

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
                    // LOGIN: Go directly to home, no setup check
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onSignUpRequested = { navController.navigate("signup") },
                viewModel = loginViewModel
            )
        }

        composable("signup") {
            SignUpScreen(
                onSignUpDone = { uid ->
                    onLoggedIn(uid)
                    // SIGN UP: Go to setup screen
                    navController.navigate("setup") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onBackToLogin = { navController.popBackStack() },
                viewModel = loginViewModel
            )
        }

        // Removed "checkSetup" composable - no longer needed

        composable("setup") {
            SetupScreen(
                onSetupComplete = { contact, address ->
                    // contact is now a single EmergencyContactInput (or null)
                    val contactPairs = if (contact != null && contact.name.isNotBlank() && contact.phone.isNotBlank()) {
                        listOf(contact. name to contact.phone)
                    } else {
                        emptyList()
                    }
                    setupViewModel.completeSetup(contactPairs, address)
                    navController.navigate("home") {
                        popUpTo("setup") { inclusive = true }
                    }
                },
                onSkipSetup = {
                    // Just navigate to home without saving anything
                    navController.navigate("home") {
                        popUpTo("setup") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {
            val contactsViewModel: TrustedContactsViewModel = viewModel()
            HomeScreen(
                onNavigateToContacts = { navController.navigate("trustedContacts") },
                onNavigateToEmergency = { navController. navigate("emergency") },
                onLogout = {
                    loginViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }

        composable("trustedContacts") {
            val contactsViewModel: TrustedContactsViewModel = viewModel()
            TrustedContactsScreen(
                onNavigateBack = { navController. popBackStack() },
                viewModel = contactsViewModel
            )
        }

        composable("navigation") { NavigationScreen() }

        composable("emergency") {
            val contactsViewModel: TrustedContactsViewModel = viewModel()
            EmergencyScreen(
                onNavigateBack = { navController.popBackStack() },
                contactsViewModel = contactsViewModel
            )
        }
    }
}
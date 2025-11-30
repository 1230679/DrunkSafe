package com.example. drunksafe.ui

import androidx.compose.runtime. Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose. composable
import com.example.drunksafe.viewmodel.TrustedContactsViewModel

@Composable
fun AppNavHost(onLoggedIn: (String) -> Unit) {
    val navController = rememberNavController()

    // Shared ViewModel for contacts
    val contactsViewModel: TrustedContactsViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { uid ->
                    onLoggedIn(uid)
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onSignUpRequested = { navController.navigate("signup") }
            )
        }
        composable("signup") {
            SignUpScreen(onSignUpDone = { uid ->
                onLoggedIn(uid)
                navController.navigate("home") { popUpTo("signup") { inclusive = true } }
            })
        }
        composable("home") {
            HomeScreen(
                onNavigateToContacts = { navController.navigate("trustedContacts") },
                onNavigateToEmergency = { navController. navigate("emergency") }
            )
        }
        composable("trustedContacts") {
            TrustedContactsScreen(
                onNavigateBack = { navController.popBackStack() },
                viewModel = contactsViewModel
            )
        }
        composable("navigation") { NavigationScreen() }
        composable("emergency") {
            EmergencyScreen(
                onNavigateBack = { navController.popBackStack() },
                contactsViewModel = contactsViewModel
            )
            }
        }
}
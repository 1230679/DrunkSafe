package com.example.drunksafe.ui

import androidx.compose. runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable

@Composable
fun AppNavHost(onLoggedIn: (String) -> Unit) {
    val navController = rememberNavController()
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
                onNavigateToContacts = { navController.navigate("trustedContacts") }
            )
        }
        composable("trustedContacts") {
            TrustedContactsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("navigation") { NavigationScreen() }
        composable("emergency") { EmergencyScreen() }
    }
}
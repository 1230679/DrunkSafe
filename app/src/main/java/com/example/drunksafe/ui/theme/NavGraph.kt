package com.example.drunksafe.ui

import androidx.compose. runtime. Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx. navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.drunksafe.viewmodel.LoginViewModel
import com. example.drunksafe.viewmodel. TrustedContactsViewModel

@Composable
fun AppNavHost(onLoggedIn: (String) -> Unit) {
    val navController = rememberNavController()

    // Shared ViewModels
    val contactsViewModel: TrustedContactsViewModel = viewModel()
    val loginViewModel: LoginViewModel = viewModel()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { uid ->
                    onLoggedIn(uid)
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onSignUpRequested = { navController.navigate("signup") },
                viewModel = loginViewModel
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignUpDone = { uid ->
                    onLoggedIn(uid)
                    navController.navigate("home") { popUpTo("signup") { inclusive = true } }
                },
                onBackToLogin = { navController.popBackStack() },
                viewModel = loginViewModel
            )
        }
        composable("home") {
            HomeScreen(
                onNavigateToContacts = { navController.navigate("trustedContacts") },
                onNavigateToEmergency = { navController.navigate("emergency") },
                onLogout = {
                    loginViewModel.signOut()
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("trustedContacts") {
            TrustedContactsScreen(
                onNavigateBack = { navController. popBackStack() },
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
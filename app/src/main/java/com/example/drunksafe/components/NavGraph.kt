package com.example. drunksafe.ui

import androidx.compose. foundation.layout. Box
import androidx.compose.foundation.layout. fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose. material.Surface
import androidx.compose.runtime.*
import androidx. compose.ui. Alignment
import androidx. compose.ui. Modifier
import androidx. compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx. navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.composable
import com.example.drunksafe.components.GoogleMapsCheckDialog
import com.example.drunksafe.ui.SignUpScreen
import com.example.drunksafe.data.ThemeMode
import com.example.drunksafe.viewmodel.LoginViewModel
import com. example.drunksafe.viewmodel. SetupState
import com.example.drunksafe.viewmodel.SetupViewModel
import com.example.drunksafe.viewmodel.TrustedContactsViewModel


import com.example.drunksafe.ui.theme.DarkBackground
import com.example.drunksafe.ui.theme.GoldAccent

/**
 * The main Navigation Host for the DrunkSafe application.
 *
 * This Composable defines the navigation graph and manages the transition between
 * different screens (destinations) in the app, such as Login, Sign Up, Setup,
 * and the main Dashboard.
 *
 * It initializes key ViewModels and handles the logic for conditionally navigating
 * users based on their authentication state and profile setup status.
 *
 * @param onLoggedIn A callback function triggered when the user successfully logs in
 * or completes the sign-up process. It receives the user's unique
 * identifier (UID) as a [String].
 */

@Composable
fun AppNavHost(
    onLoggedIn: (String) -> Unit,
    onThemeChanged: (ThemeMode) -> Unit
) {
    val navController = rememberNavController()
    val loginViewModel: LoginViewModel = viewModel()
    val setupViewModel: SetupViewModel = viewModel()

    GoogleMapsCheckDialog()

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { uid ->
                    onLoggedIn(uid)
                    navController.navigate("dashboard") {
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
            val context = androidx.compose.ui.platform.LocalContext.current
            val prefs = remember { com.example.drunksafe.data.HomeAddressPreferences(context) }
            val userRepo = remember { com.example.drunksafe.data.UserRepository() }
            val auth = remember { com.google.firebase.auth.FirebaseAuth.getInstance() }


            LaunchedEffect(Unit) {
                setupViewModel.checkIfSetupNeeded()
            }

            LaunchedEffect(Unit) {
                val uid = auth.currentUser?.uid ?: return@LaunchedEffect

                val profile = userRepo.getUserProfile(uid) ?: return@LaunchedEffect
                val address = profile.homeAddress

                val lat = profile.homeLat
                val lng = profile.homeLng

                if (!address.isNullOrBlank() && lat != null && lng != null) {
                    prefs.saveHomeAddress(address = address, lat = lat, lng = lng)
                }
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
                    // Navigate to home without saving data.
                    // The user can complete setup later via Profile.
                    navController.navigate("dashboard") {
                        popUpTo("setup") { inclusive = true }
                    }
                }
            )
        }

        composable("dashboard") {
        MapHomeScreen(
            onTakeMeHomeClick = {
                navController.navigate("route_in_progress")
            },
            onEmergencyAlertClick = {
                navController.navigate("emergency")
            },
            onCallTrustedContactsClick = {
                navController.navigate("trustedContacts")
            },
            onSettingsClick = {
                navController.navigate("settings")
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

        composable("emergency") {
            val contactsViewModel: TrustedContactsViewModel = viewModel()
            EmergencyScreen(
                onNavigateBack = { navController.popBackStack() },
                contactsViewModel = contactsViewModel
            )
        }

        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    loginViewModel.signOut()
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                },

                onOpenProfile = {
                    navController.navigate("profile")
                },
                onAddressClick = {
                   navController.navigate("edit_address")
                },
                onThemeClick = {
                    navController.navigate("theme")
                },
                onTermsClick = {
                    navController.navigate("terms")
                },
                onTestEmergencyClick = {
                    navController.navigate("emergency")
                }
            )
        }

        composable("profile") {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("edit_address") {
            HomeAddressScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("theme") {
            ThemeScreen(
                onNavigateBack = { navController.popBackStack() },
                onThemeChanged = { mode ->
                    onThemeChanged(mode)
                }
            )
        }

        composable("terms") {
            TermsPrivacyScreen(onNavigateBack = { navController.popBackStack() })
        }
    }
}
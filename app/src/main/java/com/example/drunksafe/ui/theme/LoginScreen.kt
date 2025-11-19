package com.example.drunksafe.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drunksafe.viewmodel.AuthUiState
import com.example.drunksafe.viewmodel.LoginViewModel
import com.example.drunksafe.R

@Composable
fun LoginScreen(
    onLoginSuccess: (userId: String) -> Unit,
    onSignUpRequested: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> onLoginSuccess((uiState as AuthUiState.Success).userId)
            else -> {}
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF072E3A)) {
        Column(modifier = Modifier.fillMaxSize().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(Modifier.height(48.dp))
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), // Imagem padrão verde
                contentDescription = null,
                modifier = Modifier.size(96.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text("DrunkSafe", color = Color(0xFF7FD08A), style = MaterialTheme.typography.h4)
            Spacer(Modifier.height(24.dp))

            OutlinedTextField(value = email, onValueChange = { email = it }, placeholder = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value = password, onValueChange = { password = it }, placeholder = { Text("Password") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            Spacer(Modifier.height(16.dp))

            Button(onClick = { viewModel.signIn(email.trim(), password) }, modifier = Modifier.fillMaxWidth().height(52.dp), colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD8A84A))) {
                Text("Log In", color = Color(0xFF08303A))
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onSignUpRequested, modifier = Modifier.fillMaxWidth()) {
                Text("Sign Up", color = Color(0xFFD8A84A))
            }

            if (uiState is AuthUiState.Loading) {
                Spacer(Modifier.height(12.dp))
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            (uiState as? AuthUiState.Error)?.let {
                Spacer(Modifier.height(12.dp))
                Text(text = it.message, color = Color.Red)
            }
        }
    }
}
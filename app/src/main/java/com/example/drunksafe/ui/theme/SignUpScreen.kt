package com.example.drunksafe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drunksafe.viewmodel.LoginViewModel

@Composable
fun SignUpScreen(onSignUpDone: (String) -> Unit, viewModel: LoginViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }

    LaunchedEffect(uiState) {
        if (uiState is com.example.drunksafe.viewmodel.AuthUiState.Success) {
            onSignUpDone((uiState as com.example.drunksafe.viewmodel.AuthUiState.Success).userId)
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Spacer(Modifier.height(24.dp))
        OutlinedTextField(value = displayName, onValueChange = { displayName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
        Spacer(Modifier.height(16.dp))
        Button(onClick = { viewModel.signUp(email.trim(), password, displayName.trim()) }, modifier = Modifier.fillMaxWidth()) {
            Text("Create account")
        }
        (uiState as? com.example.drunksafe.viewmodel.AuthUiState.Error)?.let { Text(it.message, color = MaterialTheme.colors.error) }
    }
}
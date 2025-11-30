package com.example. drunksafe. ui

import androidx.compose. foundation.BorderStroke
import androidx.compose.foundation. Image
import androidx.compose.foundation.layout.*
import androidx. compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation. text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons. Icons
import androidx. compose.material.icons.filled. Visibility
import androidx. compose.material.icons.filled.VisibilityOff
import androidx.compose. runtime.*
import androidx. compose.ui. Alignment
import androidx. compose.ui. Modifier
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. res.painterResource
import androidx.compose. ui.text.input.KeyboardType
import androidx.compose. ui.text.input.PasswordVisualTransformation
import androidx.compose.ui. text.input. VisualTransformation
import androidx.compose. ui.unit.dp
import androidx.compose.ui. unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drunksafe.viewmodel.AuthUiState
import com. example.drunksafe.viewmodel. LoginViewModel
import com.example.drunksafe.R

private val DarkBackground = Color(0xFF072E3A)
private val GoldAccent = Color(0xFFD8A84A)
private val GreenAccent = Color(0xFF7FD08A)

@Composable
fun LoginScreen(
    onLoginSuccess: (userId: String) -> Unit,
    onSignUpRequested: () -> Unit,
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState. collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var resetEmail by remember { mutableStateOf("") }
    var showResetConfirmation by remember { mutableStateOf(false) }

    // Validação
    val isEmailValid = remember(email) {
        email.isEmpty() || android.util. Patterns.EMAIL_ADDRESS.matcher(email). matches()
    }
    val isFormValid = email.isNotBlank() && password.isNotBlank() && isEmailValid

    LaunchedEffect(uiState) {
        when (uiState) {
            is AuthUiState.Success -> onLoginSuccess((uiState as AuthUiState.Success).userId)
            is AuthUiState. PasswordResetSent -> {
                showResetConfirmation = true
                showResetDialog = false
            }
            else -> {}
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
        Column(
            modifier = Modifier
                . fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(48.dp))

            // Logo
            Image(
                painter = painterResource(id = R.drawable.drunksafe_logo),
                contentDescription = "DrunkSafe Logo",
                modifier = Modifier.size(120.dp)  // Podes ajustar o tamanho
            )

            Spacer(Modifier.height(16.dp))
            Text("DrunkSafe", color = GreenAccent, style = MaterialTheme. typography.h4)
            Spacer(Modifier. height(24.dp))

            // Email field com validação
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Gray) },
                placeholder = { Text("Enter your email", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12. dp),
                isError = ! isEmailValid,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color.Gray,
                    errorBorderColor = Color.Red,
                    cursorColor = GoldAccent
                ),
                singleLine = true
            )

            if (! isEmailValid && email.isNotEmpty()) {
                Text(
                    "Invalid email format",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 4. dp)
                )
            }

            Spacer(Modifier. height(12.dp))

            // Password field com toggle de visibilidade
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.Gray) },
                placeholder = { Text("Enter your password", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType. Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons. Default.Visibility else Icons.Default. VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TextFieldDefaults. outlinedTextFieldColors(
                    textColor = Color. White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color. Gray,
                    cursorColor = GoldAccent
                ),
                singleLine = true
            )

            // Forgot Password
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = { showResetDialog = true }) {
                    Text("Forgot Password?", color = GoldAccent, fontSize = 12. sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Login Button
            Button(
                onClick = { viewModel.signIn(email. trim(), password) },
                modifier = Modifier
                    .fillMaxWidth()
                    . height(52.dp),
                enabled = isFormValid && uiState !is AuthUiState.Loading,                colors = ButtonDefaults.buttonColors(
                    backgroundColor = GoldAccent,
                    disabledBackgroundColor = GoldAccent.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState is AuthUiState. Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DarkBackground,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Log In", color = DarkBackground)
                }
            }

            Spacer(Modifier. height(12.dp))

            OutlinedButton(
                onClick = onSignUpRequested,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12. dp),
                border = BorderStroke(1.dp, GoldAccent)
            ) {
                Text("Sign Up", color = GoldAccent)
            }

            // Error message
            (uiState as?  AuthUiState. Error)?.let {
                Spacer(Modifier.height(16. dp))
                Card(
                    backgroundColor = Color.Red. copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it.message,
                        color = Color.Red,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }

    // Reset Password Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            backgroundColor = DarkBackground,
            title = { Text("Reset Password", color = Color.White) },
            text = {
                Column {
                    Text(
                        "Enter your email address and we'll send you a link to reset your password.",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    OutlinedTextField(
                        value = resetEmail,
                        onValueChange = { resetEmail = it },
                        label = { Text("Email", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = GoldAccent
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (resetEmail.isNotBlank()) {
                            viewModel.resetPassword(resetEmail. trim())
                        }
                    },
                    colors = ButtonDefaults. buttonColors(backgroundColor = GoldAccent),
                    enabled = resetEmail.isNotBlank()
                ) {
                    Text("Send Reset Link", color = DarkBackground)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel", color = GoldAccent)
                }
            }
        )
    }

    // Reset Confirmation Dialog
    if (showResetConfirmation) {
        AlertDialog(
            onDismissRequest = { showResetConfirmation = false },
            backgroundColor = DarkBackground,
            title = { Text("Email Sent!", color = GreenAccent) },
            text = {
                Text(
                    "Check your email for a link to reset your password.",
                    color = Color.White
                )
            },
            confirmButton = {
                Button(
                    onClick = { showResetConfirmation = false },
                    colors = ButtonDefaults.buttonColors(backgroundColor = GoldAccent)
                ) {
                    Text("OK", color = DarkBackground)
                }
            }
        )
    }
}
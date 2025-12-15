package com.example.drunksafe.ui

import androidx.compose. foundation.layout.*
import androidx. compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation. text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose. material.icons.filled.ArrowBack
import androidx.compose.material.icons. filled. Visibility
import androidx. compose.material.icons.filled.VisibilityOff
import androidx.compose. runtime.*
import androidx.compose.ui. Alignment
import androidx.compose.ui. Modifier
import androidx.compose.ui. graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text. input.KeyboardType
import androidx.compose.ui.text. input.PasswordVisualTransformation
import androidx.compose.ui. text.input.VisualTransformation
import androidx. compose.ui.unit.dp
import androidx.compose.ui. unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com. example.drunksafe.viewmodel. AuthUiState
import com.example. drunksafe. viewmodel.LoginViewModel

import com.example.drunksafe.ui.theme.DarkBackground
import com.example.drunksafe.ui.theme.GoldAccent
import com.example.drunksafe.ui.theme.GreenAccent

/**
 * The User Registration Screen.
 *
 * This screen allows new users to create an account by providing their name, email, and password.
 * It implements real-time form validation to ensure data integrity before submission.
 *
 * **Features:**
 * * **Live Validation:** Checks for valid email format and password length (min 6 chars).
 * * **Password Confirmation:** Ensures the user didn't make a typo in their password.
 * * **State Handling:** Displays loading spinners during network requests and error messages if registration fails.
 *
 * @param onSignUpDone Callback triggered when the account is successfully created. Passes the new User ID.
 * @param onBackToLogin Callback to return to the Login screen if the user already has an account.
 * @param viewModel The ViewModel handling the Firebase Authentication logic.
 */
@Composable
fun SignUpScreen(
    onSignUpDone: (String) -> Unit,
    onBackToLogin: () -> Unit = {},
    viewModel: LoginViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Validações
    val isEmailValid = remember(email) {
        email.isEmpty() || android.util.Patterns.EMAIL_ADDRESS. matcher(email).matches()
    }
    val isPasswordValid = remember(password) {
        password.isEmpty() || password.length >= 6
    }
    val doPasswordsMatch = remember(password, confirmPassword) {
        confirmPassword.isEmpty() || password == confirmPassword
    }
    val isFormValid = displayName.isNotBlank() &&
            email.isNotBlank() &&
            password.isNotBlank() &&
            confirmPassword. isNotBlank() &&
            isEmailValid &&
            isPasswordValid &&
            doPasswordsMatch

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState. Success) {
            onSignUpDone((uiState as AuthUiState.Success).userId)
        }
    }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header com botão voltar
            Row(
                modifier = Modifier. fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackToLogin) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = "Back to Login",
                        tint = Color.White
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "Create Account",
                    color = GreenAccent,
                    style = MaterialTheme. typography.h5,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(32.dp))

            // Name field
            OutlinedTextField(
                value = displayName,
                onValueChange = { displayName = it },
                label = { Text("Name", color = Color.Gray) },
                placeholder = { Text("Enter your name", color = Color. Gray) },
                modifier = Modifier. fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = GoldAccent
                ),
                singleLine = true
            )

            Spacer(Modifier.height(12. dp))

            // Email field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email", color = Color.Gray) },
                placeholder = { Text("Enter your email", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = !isEmailValid && email.isNotEmpty(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = TextFieldDefaults. outlinedTextFieldColors(
                    textColor = Color. White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color. Gray,
                    errorBorderColor = Color.Red,
                    cursorColor = GoldAccent
                ),
                singleLine = true
            )

            if (!isEmailValid && email.isNotEmpty()) {
                Text(
                    "Invalid email format",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Password field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password", color = Color.Gray) },
                placeholder = { Text("Min.  6 characters", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = !isPasswordValid && password.isNotEmpty(),
                visualTransformation = if (passwordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType. Password),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Default. Visibility else Icons. Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color.Gray,
                    errorBorderColor = Color.Red,
                    cursorColor = GoldAccent
                ),
                singleLine = true
            )

            if (!isPasswordValid && password.isNotEmpty()) {
                Text(
                    "Password must be at least 6 characters",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier
                        . fillMaxWidth()
                        .padding(start = 4. dp, top = 4.dp)
                )
            }

            Spacer(Modifier.height(12.dp))

            // Confirm Password field
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Password", color = Color.Gray) },
                placeholder = { Text("Re-enter your password", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = !doPasswordsMatch && confirmPassword.isNotEmpty(),
                visualTransformation = if (confirmPasswordVisible)
                    VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType. Password),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Icon(
                            imageVector = if (confirmPasswordVisible)
                                Icons.Default.Visibility else Icons.Default. VisibilityOff,
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                            tint = Color.Gray
                        )
                    }
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color.Gray,
                    errorBorderColor = Color.Red,
                    cursorColor = GoldAccent
                ),
                singleLine = true
            )

            if (!doPasswordsMatch && confirmPassword.isNotEmpty()) {
                Text(
                    "Passwords do not match",
                    color = Color. Red,
                    fontSize = 12. sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, top = 4. dp)
                )
            }

            Spacer(Modifier.height(24.dp))

            // Sign Up Button
            Button(
                onClick = { viewModel.signUp(email.trim(), password, displayName.trim()) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                enabled = isFormValid && uiState !is AuthUiState.Loading,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = GoldAccent,
                    disabledBackgroundColor = GoldAccent.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (uiState is AuthUiState.Loading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24. dp),
                        color = DarkBackground,
                        strokeWidth = 2. dp
                    )
                } else {
                    Text("Create Account", color = DarkBackground)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Login link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Already have an account? ", color = Color.Gray, fontSize = 14.sp)
                TextButton(onClick = onBackToLogin) {
                    Text("Log In", color = GoldAccent, fontSize = 14.sp)
                }
            }

            // Error message
            (uiState as? AuthUiState.Error)?.let {
                Spacer(Modifier.height(16.dp))
                Card(
                    backgroundColor = Color. Red.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = it.message,
                        color = Color. Red,
                        modifier = Modifier. padding(12.dp)
                    )
                }
            }
        }
    }
}
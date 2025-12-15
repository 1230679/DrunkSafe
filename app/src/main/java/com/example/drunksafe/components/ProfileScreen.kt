package com.example.drunksafe.ui

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drunksafe.ui.components.PhoneNumberInputRow
import com.example.drunksafe.ui.components.countryCodes
import com.example.drunksafe.viewmodel.ProfileViewModel
import java.util.Calendar

import com.example.drunksafe.ui.theme.BackgroundDark
import com.example.drunksafe.ui.theme.ItemBlue
import com.example.drunksafe.ui.theme.AlertRed
import com.example.drunksafe.ui.theme.GoldYellow
import com.example.drunksafe.ui.theme.GreenArrow

@Composable
fun ProfileScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var isEditing by remember { mutableStateOf(false) }

    val context = LocalContext.current

    fun openDatePicker(current: String) {
        val cal = Calendar.getInstance()
        val parts = current.split("-")
        if (parts.size == 3) {
            parts[0].toIntOrNull()?.let { cal.set(Calendar.YEAR, it) }
            parts[1].toIntOrNull()?.let { cal.set(Calendar.MONTH, it - 1) }
            parts[2].toIntOrNull()?.let { cal.set(Calendar.DAY_OF_MONTH, it) }
        }

        DatePickerDialog(
            context,
            { _, year, month, day ->
                val yyyy = year.toString().padStart(4, '0')
                val mm = (month + 1).toString().padStart(2, '0')
                val dd = day.toString().padStart(2, '0')
                viewModel.onDateOfBirthChange("$yyyy-$mm-$dd")
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                backgroundColor = BackgroundDark,
                elevation = 0.dp,
                title = { Text("Profile", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Green)
                    }
                },
                actions = {
                    IconButton(onClick = { isEditing = !isEditing; viewModel.clearMessages() }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Gold)
                    }
                }
            )
        },
        backgroundColor = BackgroundDark
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(backgroundColor = CardBlue, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    Text("Name", color = Color.Gray)
                    OutlinedTextField(
                        value = uiState.displayName,
                        onValueChange = viewModel::onDisplayNameChange,
                        enabled = isEditing,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            focusedBorderColor = Gold,
                            unfocusedBorderColor = Color.Gray,
                            disabledTextColor = Color.White,
                            disabledBorderColor = Color.DarkGray,
                            cursorColor = Gold
                        ),
                        singleLine = true
                    )

                    Text("Phone number", color = Color.Gray)

                    if (!isEditing) {
                        OutlinedTextField(
                            value = if (uiState.phoneNumber.isBlank())
                                "Not set"
                            else
                                "${uiState.phoneCountryCode} ${uiState.phoneNumber}",
                            onValueChange = {},
                            enabled = false,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                textColor = Color.White,
                                disabledTextColor = Color.White,
                                disabledBorderColor = Color.DarkGray
                            ),
                            singleLine = true
                        )
                    } else {
                        val selectedCountry =
                            countryCodes.firstOrNull { it.code == uiState.phoneCountryCode }
                                ?: countryCodes[0]

                        PhoneNumberInputRow(
                            enabled = true,
                            selectedCountry = selectedCountry,
                            phoneNumber = uiState.phoneNumber,
                            onCountrySelected = { viewModel.onPhoneCountryCodeChange(it.code) },
                            onPhoneNumberChanged = { viewModel.onPhoneNumberChange(it) },
                            borderColor = Color.Gray,
                            focusedBorderColor = Gold,
                            textColor = Color.White,
                            placeholderColor = Color.Gray,
                            cursorColor = Gold
                        )
                    }


                    Text("Date of birth", color = Color.Gray)
                    OutlinedTextField(
                        value = uiState.dateOfBirth.ifBlank { "Not set" },
                        onValueChange = {},
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            disabledTextColor = Color.White,
                            disabledBorderColor = Color.DarkGray
                        ),
                        singleLine = true
                    )

                    if (isEditing) {
                        Button(
                            onClick = { openDatePicker(uiState.dateOfBirth) },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Gold),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Edit date of birth", color = BackgroundDark, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            uiState.errorMessage?.let { Text(it, color = Color.Red) }
            uiState.successMessage?.let { Text(it, color = Color(0xFF7FD08A)) }

            if (isEditing) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(
                        onClick = { isEditing = false; viewModel.loadProfile() },
                        modifier = Modifier.weight(1f)
                    ) { Text("Cancel", color = Gold) }

                    Button(
                        onClick = { viewModel.saveProfile(); isEditing = false },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(backgroundColor = Gold)
                    ) { Text("Save", color = BackgroundDark, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
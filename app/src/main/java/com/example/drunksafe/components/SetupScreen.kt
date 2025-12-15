package com.example.drunksafe.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.drunksafe.components.CountryCode
import com.example.drunksafe.components.PhoneNumberInputRow
import com.example.drunksafe.components.countryCodes

import com.example.drunksafe.ui.theme.DarkBackground
import com.example.drunksafe.ui.theme.GoldAccent
import com.example.drunksafe.ui.theme.GreenAccent
import com.example.drunksafe.ui.theme.CardBackground

data class EmergencyContactInput(
    val name: String = "",
    val phone: String = "",
    val countryCode: CountryCode = countryCodes[0]
)

/**
 * The Setup Screen (Onboarding).
 *
 * This screen collects essential safety information from the user:
 * 1. **Emergency Contacts:** A dynamic list where users can add multiple trusted contacts.
 * 2. **Home Address:** The destination used for the "Take me Home" feature.
 *
 * It includes validation to ensure at least one valid contact and an address are provided
 * before allowing completion. It also offers a "Skip" option with a confirmation dialog.
 *
 * @param onSetupComplete Callback triggered when the user saves their data. Returns a list of contacts and the home address string.
 * @param onSkipSetup Callback triggered if the user chooses to skip the setup process.
 */
@Composable
fun SetupScreen(
    onSetupComplete: (List<EmergencyContactInput>, String) -> Unit,
    onSkipSetup: () -> Unit
) {
    var contacts by remember { mutableStateOf(listOf(EmergencyContactInput())) }
    var address by remember { mutableStateOf("") }
    var showSkipDialog by remember { mutableStateOf(false) }

    // Validation - contact is optional, but if filled, both fields must be complete
    val hasAtLeastOneValidContact = contacts.any { it.name.isNotBlank() && it.phone.isNotBlank() }

    val isFormValid = address.isNotBlank() && hasAtLeastOneValidContact

    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            backgroundColor = CardBackground,
            title = { Text("Skip Setup?", color = Color.White) },
            text = {
                Text(
                    "You can set up your emergency contact later in the app. Are you sure you want to skip for now?",
                    color = Color.Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = { showSkipDialog = false; onSkipSetup() },
                    colors = ButtonDefaults.buttonColors(backgroundColor = GoldAccent)
                ) { Text("Skip", color = DarkBackground) }
            },
            dismissButton = {
                TextButton(onClick = { showSkipDialog = false }) {
                    Text("Cancel", color = GoldAccent)
                }
            }
        )
    }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBackground) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showSkipDialog = true }) {
                    Icon(Icons.Default.Close, contentDescription = "Skip Setup", tint = Color.Gray)
                }
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "Welcome to DrunkSafe!",
                color = GreenAccent,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Let's set up your emergency contact",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(32.dp))

            // Single Emergency Contact Card
            // For each contact, show the input card
            contacts.forEachIndexed { index, contact ->
                ContactInputCard(
                    contact = contact,
                    onContactChange = { newContact ->
                        contacts = contacts.toMutableList().apply { this[index] = newContact }
                    }
                )
                Spacer(Modifier.height(16.dp))
            }

            // "Add more trusted contacts" button
            OutlinedButton(
                onClick = { contacts = contacts + EmergencyContactInput() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = GoldAccent.copy(alpha = 0.6f)
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = CardBackground.copy(alpha = 0.4f),
                    contentColor = GoldAccent
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add another contact",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Home, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Your Home Address", color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Full Address", color = Color.Gray) },
                        placeholder = { Text("Street, City, Postal Code", color = Color.Gray) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.White,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color.Gray,
                            cursorColor = GoldAccent
                        ),
                        maxLines = 3
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = {
                    val validContacts = contacts.filter { it.name.isNotBlank() && it.phone.isNotBlank() }
                    onSetupComplete(validContacts, address)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = GreenAccent,
                    disabledBackgroundColor = GreenAccent.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = DarkBackground)
                Spacer(Modifier.width(8.dp))
                Text("Complete Setup", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = { showSkipDialog = true }) {
                Text("Skip for now", color = GoldAccent, fontSize = 14.sp)
            }

            Spacer(Modifier.height(8.dp))

            Text("You can add more contacts later in Settings", color = Color.Gray, fontSize = 12.sp)

            Spacer(Modifier.height(24.dp))
        }
    }
}


/**
 * A reusable card component for entering a single contact's details.
 * Contains a name field and a phone field with a country code dropdown.
 *
 * @param contact The current state of this contact input.
 * @param onContactChange Callback to update the state of this specific contact.
 */
@Composable
fun ContactInputCard(
    contact: EmergencyContactInput,
    onContactChange: (EmergencyContactInput) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = CardBackground,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                Spacer(Modifier.width(8.dp))
                Text("Emergency Contact", color = Color.White, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = contact.name,
                onValueChange = { onContactChange(contact.copy(name = it)) },
                label = { Text("Name", color = Color.Gray) },
                placeholder = { Text("Contact name", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color.Gray,
                    cursorColor = GoldAccent
                ),
                singleLine = true
            )

            Spacer(Modifier.height(8.dp))

            PhoneNumberInputRow(
                enabled = true,
                selectedCountry = contact.countryCode,
                phoneNumber = contact.phone,
                onCountrySelected = { onContactChange(contact.copy(countryCode = it)) },
                onPhoneNumberChanged = { onContactChange(contact.copy(phone = it)) },
                borderColor = Color.Gray,
                focusedBorderColor = GoldAccent,
                textColor = Color.White,
                placeholderColor = Color.Gray,
                cursorColor = GoldAccent
            )
        }
    }
}
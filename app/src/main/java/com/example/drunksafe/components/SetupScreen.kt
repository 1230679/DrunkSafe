package com.example. drunksafe. ui

import androidx.compose. foundation.clickable
import androidx. compose.foundation.layout.*
import androidx. compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose. foundation.text.KeyboardOptions
import androidx.compose.foundation. verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose. material.icons.filled.ArrowDropDown
import androidx. compose.material.icons.filled.Check
import androidx.compose.material.icons. filled.Close
import androidx.compose.material.icons. filled.Home
import androidx.compose.material.icons. filled.Person
import androidx.compose.material.icons. filled.Phone
import androidx.compose.runtime.*
import androidx. compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose. ui.text.font.FontWeight
import androidx.compose.ui.text. input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui. unit.sp

import com.example.drunksafe.ui.theme.DarkBackground
import com.example.drunksafe.ui.theme.GoldAccent
import com.example.drunksafe.ui.theme.GreenAccent
import com.example.drunksafe.ui.theme.CardBackground


// Data class for country codes
data class CountryCode(
    val country: String,
    val code: String,
    val flag: String
)

// List of common country codes
val countryCodes = listOf(
    CountryCode("Portugal", "+351", "🇵🇹"),
    CountryCode("Spain", "+34", "🇪🇸"),
    CountryCode("France", "+33", "🇫🇷"),
    CountryCode("Germany", "+49", "🇩🇪"),
    CountryCode("Italy", "+39", "🇮🇹"),
    CountryCode("United Kingdom", "+44", "🇬🇧"),
    CountryCode("United States", "+1", "🇺🇸"),
    CountryCode("Brazil", "+55", "🇧🇷"),
    CountryCode("Netherlands", "+31", "🇳🇱"),
    CountryCode("Belgium", "+32", "🇧🇪"),
    CountryCode("Switzerland", "+41", "🇨🇭"),
    CountryCode("Ireland", "+353", "🇮🇪"),
    CountryCode("Poland", "+48", "🇵🇱"),
    CountryCode("Austria", "+43", "🇦🇹"),
    CountryCode("Sweden", "+46", "🇸🇪"),
    CountryCode("Norway", "+47", "🇳🇴"),
    CountryCode("Denmark", "+45", "🇩🇰"),
    CountryCode("Finland", "+358", "🇫🇮"),
    CountryCode("Greece", "+30", "🇬🇷"),
    CountryCode("Canada", "+1", "🇨🇦")
)

data class EmergencyContactInput(
    val name: String = "",
    val phone: String = "",
    val countryCode: CountryCode = countryCodes[0] // Default to Portugal
)

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

    // Skip confirmation dialog
    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            backgroundColor = CardBackground,
            title = { Text("Skip Setup? ", color = Color. White) },
            text = {
                Text(
                    "You can set up your emergency contact later in the app.  Are you sure you want to skip for now?",
                    color = Color. Gray
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSkipDialog = false
                        onSkipSetup()
                    },
                    colors = ButtonDefaults.buttonColors(backgroundColor = GoldAccent)
                ) {
                    Text("Skip", color = DarkBackground)
                }
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
                . fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // X button at top right
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showSkipDialog = true }) {
                    Icon(
                        Icons. Default.Close,
                        contentDescription = "Skip Setup",
                        tint = Color.Gray
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Title
            Text(
                "Welcome to DrunkSafe! ",
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                border = ButtonDefaults.outlinedBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(GoldAccent)
                )
            ) {
                Text("Add more trusted contacts", color = GoldAccent)
            }

            Spacer(Modifier.height(24.dp))

            // Home Address
            Card(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Your Home Address",
                            color = Color.White,
                            fontWeight = FontWeight. Bold
                        )
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

            // Complete Setup Button
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
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = DarkBackground
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Complete Setup",
                    color = DarkBackground,
                    fontWeight = FontWeight. Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Skip text button
            TextButton(onClick = { showSkipDialog = true }) {
                Text(
                    "Skip for now",
                    color = GoldAccent,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "You can add more contacts later in Settings",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ContactInputCard(
    contact: EmergencyContactInput,
    onContactChange: (EmergencyContactInput) -> Unit
) {
    var showCountryDropdown by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier. fillMaxWidth(),
        backgroundColor = CardBackground,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8. dp))
                Text(
                    "Emergency Contact",
                    color = Color. White,
                    fontWeight = FontWeight.Bold
                )

                // Filled indicator
                if (contact.name. isNotBlank() && contact.phone. isNotBlank()) {
                    Spacer(Modifier.weight(1f))
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Complete",
                        tint = GreenAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Name
            OutlinedTextField(
                value = contact.name,
                onValueChange = { onContactChange(contact. copy(name = it)) },
                label = { Text("Name", color = Color. Gray) },
                placeholder = { Text("Contact name", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Person, null, tint = Color. Gray)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = TextFieldDefaults. outlinedTextFieldColors(
                    textColor = Color. White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color. Gray,
                    cursorColor = GoldAccent
                ),
                singleLine = true
            )

            Spacer(Modifier. height(8.dp))

            // Phone with Country Code Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Country Code Dropdown
                Box {
                    OutlinedButton(
                        onClick = { showCountryDropdown = true },
                        modifier = Modifier
                            .width(120.dp)
                            .height(56.dp),
                        shape = RoundedCornerShape(8. dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color. Transparent,
                            contentColor = Color.White
                        ),
                        border = ButtonDefaults.outlinedBorder. copy(
                            brush = androidx.compose.ui. graphics.SolidColor(Color.Gray)
                        )
                    ) {
                        Text(
                            text = "${contact.countryCode.flag} ${contact.countryCode.code}",
                            color = Color.White,
                            fontSize = 14.sp
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Select country",
                            tint = Color.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = showCountryDropdown,
                        onDismissRequest = { showCountryDropdown = false },
                        modifier = Modifier
                            .width(250.dp)
                            .heightIn(max = 300.dp)
                    ) {
                        countryCodes.forEach { countryCode ->
                            DropdownMenuItem(
                                onClick = {
                                    onContactChange(contact. copy(countryCode = countryCode))
                                    showCountryDropdown = false
                                }
                            ) {
                                Text(
                                    text = "${countryCode.flag} ${countryCode.country} (${countryCode.code})",
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.width(8. dp))

                // Phone Number Field (numbers only)
                OutlinedTextField(
                    value = contact.phone,
                    onValueChange = { newValue ->
                        // Only allow digits
                        val digitsOnly = newValue.filter { it. isDigit() }
                        onContactChange(contact. copy(phone = digitsOnly))
                    },
                    label = { Text("Phone", color = Color.Gray) },
                    placeholder = { Text("912345678", color = Color.Gray) },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, null, tint = Color. Gray)
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType. Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = GoldAccent
                    ),
                    singleLine = true
                )
            }
        }
    }
}
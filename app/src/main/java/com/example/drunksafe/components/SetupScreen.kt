package com.example.drunksafe.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.code
import kotlin.collections.get
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
    onSetupComplete: (EmergencyContactInput?, String) -> Unit,
    onSkipSetup: () -> Unit
) {
    var contact by remember { mutableStateOf(EmergencyContactInput()) }
    var address by remember { mutableStateOf("") }
    var showSkipDialog by remember { mutableStateOf(false) }

    // Validation - contact is optional, but if filled, both fields must be complete
    val isContactValid = (contact.name. isBlank() && contact.phone.isBlank()) ||
            (contact.name. isNotBlank() && contact.phone. isNotBlank())
    val isFormValid = address.isNotBlank() && isContactValid

    // Skip confirmation dialog
    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            backgroundColor = CardBackground,
            title = { Text("Skip Setup? ", color = Color.Companion.White) },
            text = {
                Text(
                    "You can set up your emergency contact later in the app.  Are you sure you want to skip for now?",
                    color = Color.Companion.Gray
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

    Surface(modifier = Modifier.Companion.fillMaxSize(), color = DarkBackground) {
        Column(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            // X button at top right
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = { showSkipDialog = true }) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Skip Setup",
                        tint = Color.Companion.Gray
                    )
                }
            }

            Spacer(Modifier.Companion.height(8.dp))

            // Title
            Text(
                "Welcome to DrunkSafe! ",
                color = GreenAccent,
                fontSize = 24.sp,
                fontWeight = FontWeight.Companion.Bold
            )

            Spacer(Modifier.Companion.height(8.dp))

            Text(
                "Let's set up your emergency contact",
                color = Color.Companion.Gray,
                fontSize = 14.sp
            )

            Spacer(Modifier.Companion.height(32.dp))

            // Single Emergency Contact Card
            // For each contact, show the input card
            contacts.forEachIndexed { index, contact ->
                ContactInputCard(
                    contact = contact,
                    onContactChange = { newContact ->
                        contacts = contacts.toMutableList().apply { this[index] = newContact }
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
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

            Spacer(Modifier.Companion.height(24.dp))

            // Home Address
            Card(
                modifier = Modifier.Companion.fillMaxWidth(),
                backgroundColor = CardBackground,
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.Companion.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                        Icon(
                            Icons.Default.Home,
                            contentDescription = null,
                            tint = GoldAccent,
                            modifier = Modifier.Companion.size(24.dp)
                        )
                        Spacer(Modifier.Companion.width(8.dp))
                        Text(
                            "Your Home Address",
                            color = Color.Companion.White,
                            fontWeight = FontWeight.Companion.Bold
                        )
                    }

                    Spacer(Modifier.Companion.height(12.dp))

                    OutlinedTextField(
                        value = address,
                        onValueChange = { address = it },
                        label = { Text("Full Address", color = Color.Companion.Gray) },
                        placeholder = {
                            Text(
                                "Street, City, Postal Code",
                                color = Color.Companion.Gray
                            )
                        },
                        modifier = Modifier.Companion.fillMaxWidth(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            textColor = Color.Companion.White,
                            focusedBorderColor = GoldAccent,
                            unfocusedBorderColor = Color.Companion.Gray,
                            cursorColor = GoldAccent
                        ),
                        maxLines = 3
                    )
                }
            }

            Spacer(Modifier.Companion.height(32.dp))

            // Complete Setup Button
            Button(
                onClick = {
                    val contactToSave =
                        if (contact.name.isNotBlank() && contact.phone.isNotBlank()) {
                            // Combine country code with phone number
                            contact.copy(phone = "${contact.countryCode.code}${contact.phone}")
                        } else {
                            null
                        }
                    onSetupComplete(contactToSave, address)
                },
                modifier = Modifier.Companion
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = GreenAccent,
                    disabledBackgroundColor = GreenAccent.copy(alpha = 0.5f)
                ),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = null,
                    tint = DarkBackground
                )
                Spacer(Modifier.Companion.width(8.dp))
                Text(
                    "Complete Setup",
                    color = DarkBackground,
                    fontWeight = FontWeight.Companion.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.Companion.height(16.dp))

            // Skip text button
            TextButton(onClick = { showSkipDialog = true }) {
                Text(
                    "Skip for now",
                    color = GoldAccent,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.Companion.height(8.dp))

            Text(
                "You can add more contacts later in Settings",
                color = Color.Companion.Gray,
                fontSize = 12.sp
            )

            Spacer(Modifier.Companion.height(24.dp))
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
        modifier = Modifier.Companion.fillMaxWidth(),
        backgroundColor = CardBackground,
        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.Companion.padding(16.dp)) {
            Row(verticalAlignment = Alignment.Companion.CenterVertically) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.Companion.size(24.dp)
                )
                Spacer(Modifier.Companion.width(8.dp))
                Text(
                    "Emergency Contact",
                    color = Color.Companion.White,
                    fontWeight = FontWeight.Companion.Bold
                )

                // Filled indicator
                if (contact.name.isNotBlank() && contact.phone.isNotBlank()) {
                    Spacer(Modifier.Companion.weight(1f))
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Complete",
                        tint = GreenAccent,
                        modifier = Modifier.Companion.size(20.dp)
                    )
                }
            }

            Spacer(Modifier.Companion.height(12.dp))

            // Name
            OutlinedTextField(
                value = contact.name,
                onValueChange = { onContactChange(contact.copy(name = it)) },
                label = { Text("Name", color = Color.Companion.Gray) },
                placeholder = { Text("Contact name", color = Color.Companion.Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Person, null, tint = Color.Companion.Gray)
                },
                modifier = Modifier.Companion.fillMaxWidth(),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    textColor = Color.Companion.White,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color.Companion.Gray,
                    cursorColor = GoldAccent
                ),
                singleLine = true
            )

            Spacer(Modifier.Companion.height(8.dp))

            // Phone with Country Code Selector
            Row(
                modifier = Modifier.Companion.fillMaxWidth(),
                verticalAlignment = Alignment.Companion.CenterVertically
            ) {
                // Country Code Dropdown
                Box {
                    OutlinedButton(
                        onClick = { showCountryDropdown = true },
                        modifier = Modifier.Companion
                            .width(120.dp)
                            .height(56.dp),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = Color.Companion.Transparent,
                            contentColor = Color.Companion.White
                        ),
                        border = ButtonDefaults.outlinedBorder.copy(
                            brush = SolidColor(Color.Companion.Gray)
                        )
                    ) {
                        Text(
                            text = "${contact.countryCode.flag} ${contact.countryCode.code}",
                            color = Color.Companion.White,
                            fontSize = 14.sp
                        )
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Select country",
                            tint = Color.Companion.Gray
                        )
                    }

                    DropdownMenu(
                        expanded = showCountryDropdown,
                        onDismissRequest = { showCountryDropdown = false },
                        modifier = Modifier.Companion
                            .width(250.dp)
                            .heightIn(max = 300.dp)
                    ) {
                        countryCodes.forEach { countryCode ->
                            DropdownMenuItem(
                                onClick = {
                                    onContactChange(contact.copy(countryCode = countryCode))
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

                Spacer(Modifier.Companion.width(8.dp))

                // Phone Number Field (numbers only)
                OutlinedTextField(
                    value = contact.phone,
                    onValueChange = { newValue ->
                        // Only allow digits
                        val digitsOnly = newValue.filter { it.isDigit() }
                        onContactChange(contact.copy(phone = digitsOnly))
                    },
                    label = { Text("Phone", color = Color.Companion.Gray) },
                    placeholder = { Text("912345678", color = Color.Companion.Gray) },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, null, tint = Color.Companion.Gray)
                    },
                    modifier = Modifier.Companion.weight(1f),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Companion.Number),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.Companion.White,
                        focusedBorderColor = GoldAccent,
                        unfocusedBorderColor = Color.Companion.Gray,
                        cursorColor = GoldAccent
                    ),
                    singleLine = true
                )
            }
        }
    }
}
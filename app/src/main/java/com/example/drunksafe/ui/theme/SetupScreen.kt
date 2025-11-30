package com. example.drunksafe.ui

import androidx.compose. foundation.layout.*
import androidx.compose. foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation. text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose. material.icons.filled.Check
import androidx.compose.material.icons. filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons. filled.Person
import androidx.compose.material.icons.filled. Phone
import androidx. compose.runtime.*
import androidx. compose.ui. Alignment
import androidx. compose.ui. Modifier
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. text.font.FontWeight
import androidx.compose.ui.text. input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui. unit.sp

private val DarkBackground = Color(0xFF072E3A)
private val GoldAccent = Color(0xFFD8A84A)
private val GreenAccent = Color(0xFF7FD08A)
private val CardBackground = Color(0xFF0D2137)

data class EmergencyContactInput(
    val name: String = "",
    val phone: String = ""
)

@Composable
fun SetupScreen(
    onSetupComplete: (List<EmergencyContactInput>, String) -> Unit,
    onSkipSetup: () -> Unit  // ← Novo callback para saltar o setup
) {
    var contacts by remember {
        mutableStateOf(listOf(
            EmergencyContactInput(),
            EmergencyContactInput(),
            EmergencyContactInput(),
            EmergencyContactInput()
        ))
    }
    var address by remember { mutableStateOf("") }
    var showSkipDialog by remember { mutableStateOf(false) }

    // Validação
    val isFormValid = contacts.all { it.name. isNotBlank() && it.phone. isNotBlank() } && address.isNotBlank()

    // Dialog de confirmação para saltar setup
    if (showSkipDialog) {
        AlertDialog(
            onDismissRequest = { showSkipDialog = false },
            backgroundColor = CardBackground,
            title = { Text("Skip Setup? ", color = Color. White) },
            text = {
                Text(
                    "You can set up your emergency contacts later in the app.  Are you sure you want to skip for now?",
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
            // Botão X no topo direito
            Row(
                modifier = Modifier. fillMaxWidth(),
                horizontalArrangement = Arrangement. End
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

            // Título
            Text(
                "Welcome to DrunkSafe! ",
                color = GreenAccent,
                fontSize = 24. sp,
                fontWeight = FontWeight. Bold
            )

            Spacer(Modifier.height(8.dp))

            Text(
                "Let's set up your emergency contacts",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(32.dp))

            // 4 Contactos de Emergência
            contacts.forEachIndexed { index, contact ->
                ContactInputCard(
                    contactNumber = index + 1,
                    contact = contact,
                    onContactChange = { newContact ->
                        contacts = contacts.toMutableList().apply {
                            this[index] = newContact
                        }
                    }
                )
                Spacer(Modifier.height(16.dp))
            }

            Spacer(Modifier. height(16.dp))

            // Morada
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

            // Botão Continuar
            Button(
                onClick = { onSetupComplete(contacts, address) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = isFormValid,
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = GreenAccent,
                    disabledBackgroundColor = GreenAccent. copy(alpha = 0.5f)
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
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // Texto para saltar
            TextButton(onClick = { showSkipDialog = true }) {
                Text(
                    "Skip for now",
                    color = GoldAccent,
                    fontSize = 14.sp
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                "You can edit these later in Settings",
                color = Color.Gray,
                fontSize = 12.sp
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
fun ContactInputCard(
    contactNumber: Int,
    contact: EmergencyContactInput,
    onContactChange: (EmergencyContactInput) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = CardBackground,
        shape = RoundedCornerShape(12. dp)
    ) {
        Column(modifier = Modifier. padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default. Person,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "Emergency Contact $contactNumber",
                    color = Color.White,
                    fontWeight = FontWeight. Bold
                )

                // Indicador de preenchido
                if (contact.name.isNotBlank() && contact.phone. isNotBlank()) {
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

            // Nome
            OutlinedTextField(
                value = contact. name,
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

            // Telefone
            OutlinedTextField(
                value = contact.phone,
                onValueChange = { onContactChange(contact.copy(phone = it)) },
                label = { Text("Phone", color = Color.Gray) },
                placeholder = { Text("+351 9XX XXX XXX", color = Color.Gray) },
                leadingIcon = {
                    Icon(Icons. Default.Phone, null, tint = Color.Gray)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8. dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType. Phone),
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
package com.example.drunksafe.ui

import androidx.compose. foundation.layout.*
import androidx.compose. foundation.lazy.LazyColumn
import androidx.compose.foundation. lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose. material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled. Search
import androidx.compose.runtime.*
import androidx. compose.ui. Alignment
import androidx. compose.ui. Modifier
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. text.font.FontWeight
import androidx.compose.ui.unit. dp
import androidx. compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drunksafe.viewmodel.TrustedContact
import com.example.drunksafe.viewmodel.TrustedContactsViewModel

private val DarkBlue = Color(0xFF0A1929)
private val GoldYellow = Color(0xFFD4A84B)
private val CardBackground = Color(0xFF0D2137)

@Composable
fun TrustedContactsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: TrustedContactsViewModel = viewModel()
) {
    val uiState by viewModel. uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Surface(modifier = Modifier.fillMaxSize(), color = DarkBlue) {
        Column(modifier = Modifier.fillMaxSize(). padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier. fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, "Back", tint = Color.White)
                }
                Text(
                    text = "Your Trusted Contacts",
                    color = Color. White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel. updateSearchQuery(it) },
                modifier = Modifier. fillMaxWidth(),
                placeholder = { Text("Search.. .", color = Color. Gray) },
                leadingIcon = {
                    Icon(Icons.Default.Search, "Search", tint = Color.Gray)
                },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    backgroundColor = CardBackground,
                    textColor = Color.White,
                    cursorColor = GoldYellow,
                    focusedBorderColor = GoldYellow,
                    unfocusedBorderColor = Color. Transparent
                ),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Contacts list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement. spacedBy(8.dp)
            ) {
                items(viewModel.getFilteredContacts()) { contact ->
                    ContactCard(
                        contact = contact,
                        onNotifyClick = { viewModel. notifyContact(contact) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16. dp))

            // Add Contact button
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth(). height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
                shape = RoundedCornerShape(12. dp)
            ) {
                Text("ADD CONTACT", color = DarkBlue, fontWeight = FontWeight. Bold)
            }
        }
    }

    // Add Contact Dialog
    if (showAddDialog) {
        AddContactDialog(
            onDismiss = { showAddDialog = false },
            onAddContact = { name, phone ->
                viewModel. addContact(name, phone)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun ContactCard(contact: TrustedContact, onNotifyClick: () -> Unit) {
    Card(
        modifier = Modifier. fillMaxWidth(),
        backgroundColor = CardBackground,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(). padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(contact.name, color = Color.White, fontSize = 16.sp)

            Button(
                onClick = onNotifyClick,
                colors = ButtonDefaults. buttonColors(backgroundColor = GoldYellow),
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("NOTIFY", color = DarkBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun AddContactDialog(onDismiss: () -> Unit, onAddContact: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = CardBackground
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Add Trusted Contact",
                    color = Color. White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier. height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", color = Color.Gray) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        focusedBorderColor = GoldYellow,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = GoldYellow,
                        focusedLabelColor = GoldYellow
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone Number", color = Color. Gray) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        focusedBorderColor = GoldYellow,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = GoldYellow,
                        focusedLabelColor = GoldYellow
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier. height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement. End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = GoldYellow)
                    }

                    Spacer(modifier = Modifier. width(8.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAddContact(name. trim(), phone.trim())
                            }
                        },
                        colors = ButtonDefaults. buttonColors(backgroundColor = GoldYellow)
                    ) {
                        Text("Add", color = DarkBlue)
                    }
                }
            }
        }
    }
}
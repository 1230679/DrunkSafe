package com.example.drunksafe.ui

import android.content. Intent
import android.net.Uri
import androidx.compose.foundation. layout.*
import androidx.compose.foundation. lazy.LazyColumn
import androidx.compose.foundation.lazy. items
import androidx. compose.foundation.shape.RoundedCornerShape
import androidx.compose. material.*
import androidx.compose.material.icons.Icons
import androidx.compose. material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled. Call
import androidx. compose.material.icons.filled.Search
import androidx.compose.material.icons. filled.Send
import androidx.compose.runtime.*
import androidx. compose.ui. Alignment
import androidx. compose.ui. Modifier
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. platform.LocalContext
import androidx.compose. ui.text.font.FontWeight
import androidx.compose.ui.unit. dp
import androidx. compose.ui.unit.sp
import androidx.compose.ui. window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.drunksafe.data.repositories.TrustedContact
import com. example.drunksafe.viewmodel. TrustedContactsViewModel
import com.example.drunksafe.ui.theme.DarkBlue
import com.example.drunksafe.ui.theme.GoldYellow
import com.example.drunksafe.ui.theme.CardBackground

/**
 * Screen for managing Trusted Contacts.
 *
 * This screen allows the user to:
 * 1. View a list of their trusted contacts.
 * 2. Add new contacts to the list.
 * 3. Search through existing contacts.
 * 4. Quickly initiate communication (Call or SMS) with a selected contact.
 *
 * It uses a [LazyColumn] for efficient list rendering and various [Dialog] composables
 * for user interactions.
 *
 * @param onNavigateBack Callback to handle the back navigation.
 * @param viewModel The ViewModel that manages the list of contacts and search logic.
 */
@Composable
fun TrustedContactsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: TrustedContactsViewModel = viewModel()
) {
    val uiState by viewModel. uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    var showNotifyDialog by remember { mutableStateOf(false) }
    var showMessageDialog by remember { mutableStateOf(false) }
    var selectedContact by remember { mutableStateOf<TrustedContact?>(null) }

    val context = LocalContext. current

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
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Search bar
            OutlinedTextField(
                value = uiState. searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
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
                shape = RoundedCornerShape(12. dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16. dp))

            // Contacts list
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement. spacedBy(8.dp)
            ) {
                items(viewModel.getFilteredContacts()) { contact ->
                    ContactCard(
                        contact = contact,
                        onNotifyClick = {
                            selectedContact = contact
                            showNotifyDialog = true
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16. dp))

            // Add Contact button
            Button(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth(). height(56.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
                shape = RoundedCornerShape(12.dp)
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

    // Notify Options Dialog (Call or Message)
    if (showNotifyDialog && selectedContact != null) {
        NotifyOptionsDialog(
            contact = selectedContact!! ,
            onDismiss = {
                showNotifyDialog = false
                selectedContact = null
            },
            onCallClick = {
                // Open phone dialer
                val intent = Intent(Intent. ACTION_DIAL).apply {
                    data = Uri. parse("tel:${selectedContact!!.phoneNumber}")
                }
                context.startActivity(intent)
                showNotifyDialog = false
                selectedContact = null
            },
            onMessageClick = {
                showNotifyDialog = false
                showMessageDialog = true
            }
        )
    }

    // Send Message Dialog
    if (showMessageDialog && selectedContact != null) {
        SendMessageDialog(
            contact = selectedContact!! ,
            onDismiss = {
                showMessageDialog = false
                selectedContact = null
            },
            onSendMessage = { message ->
                // Open SMS app with pre-filled message
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:${selectedContact!!.phoneNumber}")
                    putExtra("sms_body", message)
                }
                context. startActivity(intent)
                showMessageDialog = false
                selectedContact = null
            }
        )
    }
}

/**
 * A single item row in the contacts list.
 * Displays the contact name and a "Notify" button.
 */
@Composable
fun ContactCard(contact: TrustedContact, onNotifyClick: () -> Unit) {
    Card(
        modifier = Modifier. fillMaxWidth(),
        backgroundColor = CardBackground,
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier. fillMaxWidth(). padding(horizontal = 16.dp, vertical = 12.dp),
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

/**
 * Dialog to choose between calling or messaging a contact.
 */
@Composable
fun NotifyOptionsDialog(
    contact: TrustedContact,
    onDismiss: () -> Unit,
    onCallClick: () -> Unit,
    onMessageClick: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                . fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = CardBackground
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Notify ${contact.name}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier. height(8.dp))

                Text(
                    text = contact.phoneNumber,
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Call Button
                Button(
                    onClick = onCallClick,
                    modifier = Modifier.fillMaxWidth(). height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default.Call,
                        contentDescription = "Call",
                        tint = DarkBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("CALL", color = DarkBlue, fontWeight = FontWeight. Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Message Button
                Button(
                    onClick = onMessageClick,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        Icons.Default. Send,
                        contentDescription = "Message",
                        tint = DarkBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8. dp))
                    Text("MESSAGE", color = DarkBlue, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel Button
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel", color = GoldYellow)
                }
            }
        }
    }
}

/**
 * Dialog for composing an SMS message.
 * Includes pre-defined quick messages for safety scenarios.
 */
@Composable
fun SendMessageDialog(
    contact: TrustedContact,
    onDismiss: () -> Unit,
    onSendMessage: (String) -> Unit
) {
    var message by remember { mutableStateOf("") }

    // Pre-defined quick messages
    val quickMessages = listOf(
        "I need help, please call me! ",
        "Can you come pick me up?",
        "I'm not feeling safe, please check on me."
    )

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
                    text = "Send Message to ${contact.name}",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Quick message buttons
                Text(
                    text = "Quick Messages:",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                quickMessages.forEach { quickMessage ->
                    OutlinedButton(
                        onClick = { message = quickMessage },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8. dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            backgroundColor = if (message == quickMessage) GoldYellow. copy(alpha = 0.2f) else Color. Transparent
                        ),
                        border = ButtonDefaults.outlinedBorder. copy(
                            brush = androidx.compose.ui. graphics.SolidColor(
                                if (message == quickMessage) GoldYellow else Color.Gray
                            )
                        )
                    ) {
                        Text(
                            text = quickMessage,
                            color = if (message == quickMessage) GoldYellow else Color.White,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16. dp))

                // Custom message input
                Text(
                    text = "Or write your own:",
                    color = Color.Gray,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.fillMaxWidth(). height(120.dp),
                    placeholder = { Text("Type your message.. .", color = Color. Gray) },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        textColor = Color.White,
                        focusedBorderColor = GoldYellow,
                        unfocusedBorderColor = Color.Gray,
                        cursorColor = GoldYellow
                    ),
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement. End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = GoldYellow)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (message.isNotBlank()) {
                                onSendMessage(message)
                            }
                        },
                        enabled = message.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = GoldYellow,
                            disabledBackgroundColor = GoldYellow.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = DarkBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Send", color = DarkBlue)
                    }
                }
            }
        }
    }
}

/**
 * Dialog form to input Name and Phone Number for a new contact.
 */
@Composable
fun AddContactDialog(onDismiss: () -> Unit, onAddContact: (String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                . padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            backgroundColor = CardBackground
        ) {
            Column(modifier = Modifier. padding(24.dp)) {
                Text(
                    text = "Add Trusted Contact",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight. Bold
                )

                Spacer(modifier = Modifier. height(16.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name", color = Color. Gray) },
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
                    label = { Text("Phone Number", color = Color.Gray) },
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

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier. fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel", color = GoldYellow)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                onAddContact(name. trim(), phone.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(backgroundColor = GoldYellow)
                    ) {
                        Text("Add", color = DarkBlue)
                    }
                }
            }
        }
    }
}
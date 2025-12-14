package com.example.drunksafe.data.services


import androidx.compose. foundation.layout.*
import androidx. compose.foundation.shape.RoundedCornerShape
import androidx.compose. foundation.text.KeyboardOptions
import androidx. compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose. material.icons.filled.ArrowDropDown
import androidx. compose.material.icons.filled.Phone
import androidx.compose.runtime.*
import androidx. compose.ui. Alignment
import androidx. compose.ui. Modifier
import androidx. compose.ui.graphics.Color
import androidx.compose.ui. graphics.SolidColor
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose. ui.unit.dp
import androidx.compose.ui. unit.sp

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

/**
 * Reusable phone input with country code selector
 * @param phoneNumber The phone number without country code (digits only)
 * @param countryCode The selected country code
 * @param onPhoneNumberChange Callback when phone number changes
 * @param onCountryCodeChange Callback when country code changes
 * @param focusedBorderColor Color for focused border
 * @param unfocusedBorderColor Color for unfocused border
 * @param textColor Color for text
 * @param modifier Modifier for the component
 */
@Composable
fun PhoneInputWithCountryCode(
    phoneNumber: String,
    countryCode: CountryCode,
    onPhoneNumberChange: (String) -> Unit,
    onCountryCodeChange: (CountryCode) -> Unit,
    focusedBorderColor: Color,
    unfocusedBorderColor: Color = Color.Gray,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    var showCountryDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = modifier. fillMaxWidth(),
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
                colors = ButtonDefaults. outlinedButtonColors(
                    backgroundColor = Color. Transparent,
                    contentColor = textColor
                ),
                border = ButtonDefaults.outlinedBorder. copy(
                    brush = SolidColor(unfocusedBorderColor)
                )
            ) {
                Text(
                    text = "${countryCode.flag} ${countryCode.code}",
                    color = textColor,
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
                countryCodes.forEach { code ->
                    DropdownMenuItem(
                        onClick = {
                            onCountryCodeChange(code)
                            showCountryDropdown = false
                        }
                    ) {
                        Text(
                            text = "${code.flag} ${code.country} (${code.code})",
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        // Phone Number Field (numbers only)
        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { newValue ->
                // Only allow digits
                val digitsOnly = newValue.filter { it. isDigit() }
                onPhoneNumberChange(digitsOnly)
            },
            label = { Text("Phone", color = Color.Gray) },
            placeholder = { Text("912345678", color = Color.Gray) },
            leadingIcon = {
                Icon(Icons.Default.Phone, null, tint = Color.Gray)
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8. dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType. Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = textColor,
                focusedBorderColor = focusedBorderColor,
                unfocusedBorderColor = unfocusedBorderColor,
                cursorColor = focusedBorderColor
            ),
            singleLine = true
        )
    }
}

/**
 * Combines country code with phone number
 */
fun formatFullPhoneNumber(countryCode: CountryCode, phoneNumber: String): String {
    return "${countryCode.code}${phoneNumber}"
}
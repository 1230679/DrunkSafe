package com.example.drunksafe.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class CountryCode(
    val country: String,
    val code: String,
    val flag: String
)

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
 * Reusable phone input row: country dropdown + digits-only phone text field.
 *
 * - enabled=false: dropdown + field are disabled (read-only mode)
 * - phoneNumberChanged: gets digits-only string
 */
@Composable
fun PhoneNumberInputRow(
    enabled: Boolean,
    selectedCountry: CountryCode,
    phoneNumber: String,
    onCountrySelected: (CountryCode) -> Unit,
    onPhoneNumberChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    borderColor: Color = Color.Gray,
    focusedBorderColor: Color = Color(0xFFD8A84A),
    textColor: Color = Color.White,
    placeholderColor: Color = Color.Gray,
    disabledBorderColor: Color = Color.DarkGray,
    cursorColor: Color = Color(0xFFD8A84A)
) {
    var showCountryDropdown by remember { mutableStateOf(false) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            OutlinedButton(
                onClick = { if (enabled) showCountryDropdown = true },
                modifier = Modifier
                    .width(120.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = enabled,
                colors = ButtonDefaults.outlinedButtonColors(
                    backgroundColor = Color.Transparent,
                    contentColor = textColor
                ),
                border = ButtonDefaults.outlinedBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(borderColor)
                )
            ) {
                Text(
                    text = "${selectedCountry.flag} ${selectedCountry.code}",
                    color = textColor,
                    fontSize = 14.sp
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select country",
                    tint = placeholderColor
                )
            }

            DropdownMenu(
                expanded = showCountryDropdown,
                onDismissRequest = { showCountryDropdown = false },
                modifier = Modifier
                    .width(250.dp)
                    .heightIn(max = 300.dp)
            ) {
                countryCodes.forEach { cc ->
                    DropdownMenuItem(
                        onClick = {
                            onCountrySelected(cc)
                            showCountryDropdown = false
                        }
                    ) {
                        Text(text = "${cc.flag} ${cc.country} (${cc.code})", fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        OutlinedTextField(
            value = phoneNumber,
            onValueChange = { raw ->
                if (!enabled) return@OutlinedTextField
                val digitsOnly = raw.filter { it.isDigit() }
                onPhoneNumberChanged(digitsOnly)
            },
            label = { Text("Phone", color = placeholderColor) },
            placeholder = { Text("912345678", color = placeholderColor) },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(8.dp),
            enabled = enabled,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                textColor = textColor,
                focusedBorderColor = focusedBorderColor,
                unfocusedBorderColor = borderColor,
                cursorColor = cursorColor,
                disabledTextColor = textColor,
                disabledBorderColor = disabledBorderColor
            ),
            singleLine = true
        )
    }
}
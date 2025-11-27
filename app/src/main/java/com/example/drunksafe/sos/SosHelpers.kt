package com.example.drunksafe.sos

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.drunksafe.R

/**
 * Helper class for SOS functionality.
 * 
 * This class provides utility methods for:
 * - Reading emergency contacts from SharedPreferences
 * - Sending SMS messages with location to contacts
 * - Initiating emergency phone calls
 * 
 * CUSTOMIZATION NOTES:
 * - If the app already has a contacts manager or database, replace [getEmergencyContacts]
 *   with a call to that existing data source.
 * - The SharedPreferences key "emergency_contacts" expects comma-separated phone numbers.
 * - Example: "112,+1234567890,+0987654321"
 */
object SosHelpers {

    private const val PREFS_NAME = "drunksafe_prefs"
    // Key for storing emergency contacts as comma-separated phone numbers
    // CUSTOMIZE: Change this key or integrate with existing contact storage if available
    private const val KEY_EMERGENCY_CONTACTS = "emergency_contacts"
    
    // Emergency number to call (112 is the European emergency number)
    const val EMERGENCY_NUMBER = "112"

    /**
     * Retrieves emergency contacts from SharedPreferences.
     * 
     * CUSTOMIZATION: If the app has an existing contact manager (e.g., Firestore-based
     * TrustedContactsScreen), integrate with that instead of SharedPreferences.
     * 
     * @param context Application context
     * @return List of phone numbers, or empty list if none found
     */
    fun getEmergencyContacts(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val contactsString = prefs.getString(KEY_EMERGENCY_CONTACTS, null)
        
        return if (contactsString.isNullOrBlank()) {
            emptyList()
        } else {
            contactsString.split(",")
                .map { it.trim() }
                .filter { it.isNotBlank() }
        }
    }

    /**
     * Saves emergency contacts to SharedPreferences (for testing/setup purposes).
     * 
     * @param context Application context
     * @param contacts List of phone numbers to save
     */
    fun saveEmergencyContacts(context: Context, contacts: List<String>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_EMERGENCY_CONTACTS, contacts.joinToString(","))
            .apply()
    }

    /**
     * Sends an SMS message to a single contact.
     * 
     * @param context Application context
     * @param phoneNumber The recipient's phone number
     * @param message The message to send
     * @return true if SMS was sent successfully, false otherwise
     */
    fun sendSms(context: Context, phoneNumber: String, message: String): Boolean {
        return try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.SEND_SMS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }

            val smsManager = SmsManager.getDefault()
            // Split message if it exceeds SMS length limit
            val parts = smsManager.divideMessage(message)
            smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Sends emergency SMS to all emergency contacts.
     * 
     * @param context Application context
     * @param location User's current location (can be null)
     * @return Number of successfully sent messages
     */
    fun sendEmergencySmsToAllContacts(context: Context, location: Location?): Int {
        val contacts = getEmergencyContacts(context)
        if (contacts.isEmpty()) {
            return 0
        }

        val message = buildSmsMessage(context, location)
        var successCount = 0

        for (contact in contacts) {
            if (sendSms(context, contact, message)) {
                successCount++
            }
        }

        return successCount
    }

    /**
     * Builds the emergency SMS message with location information.
     * 
     * @param context Application context
     * @param location User's current location (can be null)
     * @return Formatted emergency message
     */
    fun buildSmsMessage(context: Context, location: Location?): String {
        return if (location != null) {
            val lat = String.format("%.6f", location.latitude)
            val lng = String.format("%.6f", location.longitude)
            val mapsLink = "https://maps.google.com/?q=${location.latitude},${location.longitude}"
            context.getString(R.string.sos_sms_message, lat, lng, mapsLink)
        } else {
            context.getString(R.string.sos_sms_message_no_location)
        }
    }

    /**
     * Initiates a phone call to the emergency number.
     * Uses ACTION_CALL to immediately dial (requires CALL_PHONE permission).
     * 
     * @param context Application context
     * @param phoneNumber The number to call (defaults to EMERGENCY_NUMBER)
     * @return true if call was initiated, false otherwise
     */
    fun initiateEmergencyCall(context: Context, phoneNumber: String = EMERGENCY_NUMBER): Boolean {
        return try {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }

            val callIntent = Intent(Intent.ACTION_CALL).apply {
                data = Uri.parse("tel:$phoneNumber")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(callIntent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    /**
     * Gets the user's last known location using LocationManager.
     * This is a fallback if FusedLocationProviderClient is not available.
     * 
     * @param context Application context
     * @return Last known location, or null if unavailable
     */
    fun getLastKnownLocation(context: Context): Location? {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return null
        }

        return try {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            
            // Try GPS first, then network
            var location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location == null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
            location
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Shows a toast message.
     * 
     * @param context Application context
     * @param message Message to display
     */
    fun showToast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    /**
     * Shows a toast message from a string resource.
     * 
     * @param context Application context
     * @param resId String resource ID
     */
    fun showToast(context: Context, resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_LONG).show()
    }
}

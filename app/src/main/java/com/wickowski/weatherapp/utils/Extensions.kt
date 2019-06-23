package com.wickowski.weatherapp.utils

import android.widget.Toast
import com.google.android.gms.location.LocationSettingsStatusCodes
import android.content.IntentSender
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.common.api.ApiException
import android.location.LocationManager
import com.google.android.gms.location.LocationServices
import android.content.Context.LOCATION_SERVICE
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.wickowski.weatherapp.R


fun AppCompatActivity.turnGPSOn(requestCode: Int, gpsStatusChanged: (Boolean) -> Unit) {
    val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
        gpsStatusChanged.invoke(true)
    } else {
        LocationServices.getSettingsClient(this)
            .checkLocationSettings(LocationUtils.buildLocationSettingsRequest())
            .addOnSuccessListener(this) { gpsStatusChanged.invoke(true) }
            .addOnFailureListener(this) { exception ->
                when ((exception as ApiException).statusCode) {
                    LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> try {
                        // Show the dialog by calling startResolutionForResult(), and check the
                        // result in onActivityResult().
                        val rae = exception as ResolvableApiException
                        rae.startResolutionForResult(this, requestCode)
                    } catch (sie: IntentSender.SendIntentException) {
                        Log.i("ERR", "PendingIntent unable to execute request.")
                    }

                    LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                        val errorMessage = getString(R.string.error_settings_change_unavailable)
                        Log.e("ERR", errorMessage)
                        showToast(errorMessage)
                    }
                }
            }
    }
}

fun Fragment.showToast(message: String) = Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

fun AppCompatActivity.showToast(message: String) = Toast.makeText(this, message, Toast.LENGTH_SHORT).show()


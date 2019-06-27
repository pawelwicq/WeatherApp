package com.wickowski.weatherapp.utils

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationSettingsRequest

object LocationUtils {

    fun buildLocationSettingsRequest(): LocationSettingsRequest = buildLocationRequest().let {
        LocationSettingsRequest.Builder()
            .setAlwaysShow(true)
            .addLocationRequest(it)
            .build()
    }

    fun buildLocationRequest(): LocationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 400
    }

}
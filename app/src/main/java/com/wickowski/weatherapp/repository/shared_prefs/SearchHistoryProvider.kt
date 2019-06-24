package com.wickowski.weatherapp.repository.shared_prefs

import android.content.SharedPreferences

private val SHARED_PREFS_LAST_CITY_ID = "LAST_CITY_ID"

class SearchHistoryProvider(private val prefs: SharedPreferences) {

    var lastSearchCityId: String
        get() = prefs.getString(SHARED_PREFS_LAST_CITY_ID, null) ?: ""
        set(value) = prefs.edit().putString(SHARED_PREFS_LAST_CITY_ID, value).apply()

}
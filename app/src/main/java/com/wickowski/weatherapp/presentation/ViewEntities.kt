package com.wickowski.weatherapp.presentation

import androidx.annotation.StringRes

data class CityCurrentWeather(
    val cityId: String,
    val cityName: String,
    val temperature: Int,
    val windSpeed: Double,
    val airHumidity: Int,
    @StringRes val conditionStringRes: Int,
    val pressure: Int,
    val weatherIcon: String
)



package com.wickowski.weatherapp.presentation

import androidx.annotation.StringRes

data class CityWeatherForecast(
    val cityName: String,
    val temperature: Int,
    @StringRes val conditionStringRes: Int,
    val pressure: Int,
    val weatherIcon: String
)



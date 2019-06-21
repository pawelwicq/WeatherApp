package com.wickowski.weatherapp.repository.net

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherForecast(val id: String, val name: String)
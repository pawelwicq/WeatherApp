package com.wickowski.weatherapp.repository.net

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherForecast(
    @Json(name = "id") val cityId: String,
    @Json(name = "name") val cityName: String,
    @Json(name = "coord") val cityLocation: CityLocation,
    val weather: Weather
)

data class CityLocation(
    @Json(name = "lat") val latitude: Double,
    @Json(name = "lon") val longitude: Double
)

data class Weather(
    val id: String,
    @Json(name = "main") val condition: WeatherCondition,
    val description: String
)

enum class WeatherCondition {
    @Json(name = "Thunderstorm") THUNDERSTORM,
    @Json(name = "Drizzle") DRIZZLE,
    @Json(name = "Rain") RAIN,
    @Json(name = "Snow") SNOW,
    @Json(name = "Mist") MIST,
    @Json(name = "Smoke") SMOKE,
    @Json(name = "Haze") HAZE,
    @Json(name = "Dust") DUST,
    @Json(name = "Fog") FOG,
    @Json(name = "Sand") SAND,
    @Json(name = "Ash") ASH,
    @Json(name = "Squall") SQUALL,
    @Json(name = "Tornado") TORNADO,
    @Json(name = "Clear") CLEAR,
    @Json(name = "Clouds") CLOUDS
}
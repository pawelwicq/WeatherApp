package com.wickowski.weatherapp.repository.net

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class WeatherForecast(
    @Json(name = "id") val cityId: String,
    @Json(name = "name") val cityName: String,
    val weather: List<Weather>,
    val main: MainForecastInfo,
    val wind: WindInfo
)

data class MainForecastInfo(
    @Json(name = "temp") val temp: Double,
    val humidity: Int,
    val pressure: Int
)

data class WindInfo(val speed: Double)

data class Weather(
    val id: String,
    @Json(name = "main") val condition: WeatherCondition,
    val icon: String,
    val description: String
)

enum class WeatherCondition {
    @Json(name = "Thunderstorm")
    THUNDERSTORM,
    @Json(name = "Drizzle")
    DRIZZLE,
    @Json(name = "Rain")
    RAIN,
    @Json(name = "Snow")
    SNOW,
    @Json(name = "Mist")
    MIST,
    @Json(name = "Smoke")
    SMOKE,
    @Json(name = "Haze")
    HAZE,
    @Json(name = "Dust")
    DUST,
    @Json(name = "Fog")
    FOG,
    @Json(name = "Sand")
    SAND,
    @Json(name = "Ash")
    ASH,
    @Json(name = "Squall")
    SQUALL,
    @Json(name = "Tornado")
    TORNADO,
    @Json(name = "Clear")
    CLEAR,
    @Json(name = "Clouds")
    CLOUDS
}
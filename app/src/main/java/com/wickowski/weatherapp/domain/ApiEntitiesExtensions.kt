package com.wickowski.weatherapp.domain

import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import com.wickowski.weatherapp.repository.net.WeatherCondition
import com.wickowski.weatherapp.repository.net.WeatherForecast
import kotlin.math.roundToInt

fun WeatherForecast.toCityWeatherForecast() = CityCurrentWeather(
    cityId = cityId,
    cityName = cityName,
    temperature = main.temp.roundToInt(),
    windSpeed = wind.speed,
    airHumidity = main.humidity,
    conditionStringRes = weather[0].condition.stringResource(),
    pressure = main.pressure,
    weatherIcon = "${weather[0].icon}.json"
)


fun WeatherCondition.stringResource() = when (this) {
    WeatherCondition.THUNDERSTORM -> R.string.thunderstorm
    WeatherCondition.DRIZZLE -> R.string.drizzle
    WeatherCondition.RAIN -> R.string.rain
    WeatherCondition.SNOW -> R.string.snow
    WeatherCondition.MIST -> R.string.mist
    WeatherCondition.SMOKE -> R.string.smoke
    WeatherCondition.HAZE -> R.string.haze
    WeatherCondition.DUST -> R.string.dust
    WeatherCondition.FOG -> R.string.fog
    WeatherCondition.SAND -> R.string.sand
    WeatherCondition.ASH -> R.string.ash
    WeatherCondition.SQUALL -> R.string.squall
    WeatherCondition.TORNADO -> R.string.tornado
    WeatherCondition.CLEAR -> R.string.clear
    WeatherCondition.CLOUDS -> R.string.clouds
}
package com.wickowski.weatherapp.domain

import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.presentation.CityWeatherForecast
import com.wickowski.weatherapp.repository.net.WeatherCondition
import com.wickowski.weatherapp.repository.net.WeatherForecast
import kotlin.math.roundToInt

fun WeatherForecast.toCityWeatherForecast(): CityWeatherForecast {
    val cityName = cityName
    val temperature = main.temp.roundToInt()
    val conditionDescription = weather[0].condition
    val pressure = main.pressure
    val iconResource = "${weather[0].icon}.json"

    return CityWeatherForecast(cityName, temperature, "", pressure, iconResource)
}

fun WeatherCondition.stringResource() = when(this) {
    WeatherCondition.THUNDERSTORM -> R.string.thunderstorm
    WeatherCondition.DRIZZLE -> R.string.drizzle
    WeatherCondition.RAIN -> R.string.drizzle
    WeatherCondition.SNOW -> R.string.drizzle
    WeatherCondition.MIST -> R.string.drizzle
    WeatherCondition.SMOKE -> R.string.drizzle
    WeatherCondition.HAZE -> R.string.drizzle
    WeatherCondition.DUST -> R.string.drizzle
    WeatherCondition.FOG -> R.string.drizzle
    WeatherCondition.SAND -> R.string.drizzle
    WeatherCondition.ASH -> R.string.drizzle
    WeatherCondition.SQUALL -> R.string.drizzle
    WeatherCondition.TORNADO -> R.string.drizzle
    WeatherCondition.CLEAR -> R.string.drizzle
    WeatherCondition.CLOUDS -> R.string.drizzle
}
package com.wickowski.weatherapp.domain.weather

import com.wickowski.weatherapp.presentation.CityWeatherForecast
import com.wickowski.weatherapp.repository.net.QueryBuilder
import com.wickowski.weatherapp.repository.net.RemoteDataSource
import com.wickowski.weatherapp.repository.net.WeatherForecast
import com.wickowski.weatherapp.repository.shared_prefs.SearchHistoryProvider
import io.reactivex.Single
import kotlin.math.roundToInt

class GetWeatherForecastUseCase(
    private val api: RemoteDataSource
) {

    fun executeForCityName(searchQuery: String): Single<WeatherForecast> = api.getWeatherForecast(
        QueryBuilder()
            .searchWithCityName(searchQuery)
            .build()
    )

    fun executeForLocation(lat: Double, lon: Double): Single<WeatherForecast> = api.getWeatherForecast(
        QueryBuilder()
            .searchWithLocation(lat, lon)
            .build()
    )

    fun executeForCityId(cityId: String): Single<WeatherForecast> = api.getWeatherForecast(
        QueryBuilder()
            .searchWithCityId(cityId)
            .build()
    )

}
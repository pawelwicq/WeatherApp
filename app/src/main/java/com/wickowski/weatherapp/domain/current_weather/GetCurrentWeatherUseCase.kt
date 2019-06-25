package com.wickowski.weatherapp.domain.current_weather

import com.wickowski.weatherapp.domain.toCityWeatherForecast
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import com.wickowski.weatherapp.repository.net.QueryBuilder
import com.wickowski.weatherapp.repository.net.RemoteDataSource
import io.reactivex.Single

class GetCurrentWeatherUseCase(
    private val api: RemoteDataSource
) {

    fun executeForCityName(searchQuery: String): Single<CityCurrentWeather> = api.getWeatherForecast(
        QueryBuilder()
            .searchWithCityName(searchQuery)
            .build()
    ).map {
        it.toCityWeatherForecast()
    }

    fun executeForLocation(lat: Double, lon: Double): Single<CityCurrentWeather> = api.getWeatherForecast(
        QueryBuilder()
            .searchWithLocation(lat, lon)
            .build()
    ).map {
        it.toCityWeatherForecast()
    }

    fun executeForCityId(cityId: String): Single<CityCurrentWeather> = api.getWeatherForecast(
        QueryBuilder()
            .searchWithCityId(cityId)
            .build()
    ).map {
        it.toCityWeatherForecast()
    }

}
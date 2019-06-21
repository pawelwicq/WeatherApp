package com.wickowski.weatherapp.domain.weather

import com.wickowski.weatherapp.repository.net.QueryBuilder
import com.wickowski.weatherapp.repository.net.RemoteDataSource
import com.wickowski.weatherapp.repository.net.WeatherForecast
import io.reactivex.Single

class GetWeatherForecastUseCase(private val api: RemoteDataSource) {

    fun execute(searchQuery: String): Single<WeatherForecast> = api.getWeatherForecast(
        QueryBuilder()
            .searchWithCityName(searchQuery)
            .build()
    )

}
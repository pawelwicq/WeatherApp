package com.wickowski.weatherapp.domain.weather

import com.wickowski.weatherapp.repository.net.QueryBuilder
import com.wickowski.weatherapp.repository.net.RemoteDataSource

class GetWeatherUseCase(private val api: RemoteDataSource) {

    fun execute(searchQuery: String) = api.queryForWeather(
        QueryBuilder()
            .search(searchQuery)
            .build()
    )

}
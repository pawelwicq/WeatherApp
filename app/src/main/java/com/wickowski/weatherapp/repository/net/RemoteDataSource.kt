package com.wickowski.weatherapp.repository.net

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface RemoteDataSource {

    @GET("weather")
    fun getWeatherForecast(@QueryMap queryMap: Map<String, String>): Single<WeatherForecast>

}
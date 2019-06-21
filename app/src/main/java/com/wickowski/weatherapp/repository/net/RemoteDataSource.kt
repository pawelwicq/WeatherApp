package com.wickowski.weatherapp.repository.net

import com.squareup.moshi.JsonClass
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface RemoteDataSource {

    @GET("weather")
    fun queryForWeather(@QueryMap queryMap: Map<String, String>): Single<Weather>

}

@JsonClass(generateAdapter = true)
data class Weather(val id: String, val name: String)
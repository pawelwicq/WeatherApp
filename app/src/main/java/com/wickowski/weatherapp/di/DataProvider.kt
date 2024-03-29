package com.wickowski.weatherapp.di

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.wickowski.weatherapp.BuildConfig
import com.wickowski.weatherapp.repository.net.RemoteDataSource
import com.wickowski.weatherapp.repository.net.interceptors.QueryParamsInterceptor
import com.wickowski.weatherapp.repository.shared_prefs.SearchHistoryProvider
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.*
import java.util.concurrent.TimeUnit

private const val WEATHER_SHARED_PREFS = "weather_preferences"

internal fun createSearchHistoryProvider(context: Context) =
    SearchHistoryProvider(context.getSharedPreferences(WEATHER_SHARED_PREFS, Context.MODE_PRIVATE))

internal fun createMoshiInstance() = Moshi.Builder()
    .add(Date::class.java, Rfc3339DateJsonAdapter())
    .add(KotlinJsonAdapterFactory())
    .build()

internal fun createWeatherApiInstance(context: Context): RemoteDataSource {

    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

    val cacheFile = File(context.cacheDir, "apiResponses")

    val builder = OkHttpClient.Builder()
        .readTimeout(10L, TimeUnit.SECONDS)
        .addInterceptor(QueryParamsInterceptor())
        .cache(Cache(cacheFile, 10 * 1024 * 1024))

    if (BuildConfig.DEBUG) builder.addInterceptor(loggingInterceptor)

    val apiClient = builder.build()

    return Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(createMoshiInstance()).withNullSerialization())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .baseUrl(BuildConfig.API_SERVER)
        .client(apiClient)
        .build()
        .create(RemoteDataSource::class.java)
}
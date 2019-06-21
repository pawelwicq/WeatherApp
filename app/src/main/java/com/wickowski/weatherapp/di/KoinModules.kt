package com.wickowski.weatherapp.di

import com.wickowski.weatherapp.repository.net.WeatherApiProvider
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val dataModule = module {
    single { WeatherApiProvider.provide(androidContext()) }
}
package com.wickowski.weatherapp.di

import com.wickowski.weatherapp.domain.weather.GetWeatherUseCase
import com.wickowski.weatherapp.presentation.MainViewModel
import com.wickowski.weatherapp.repository.net.WeatherApiProvider
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single { WeatherApiProvider.provide(androidContext()) }
}

val useCasesModule = module {
    single { GetWeatherUseCase(get()) }
}

val viewModelsModule = module {
    viewModel { MainViewModel(get()) }
}
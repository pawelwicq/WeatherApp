package com.wickowski.weatherapp.di

import com.wickowski.weatherapp.domain.weather.GetLastSearchCityIdUseCase
import com.wickowski.weatherapp.domain.weather.GetWeatherForecastUseCase
import com.wickowski.weatherapp.domain.weather.SaveLastSearchCityIdUseCase
import com.wickowski.weatherapp.presentation.search.WeatherSearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single { createWeatherApiInstance(androidContext()) }
    single { createSearchHistoryProvider(androidContext()) }
}

val useCasesModule = module {
    single { GetWeatherForecastUseCase(get()) }
    single { GetLastSearchCityIdUseCase(get()) }
    single { SaveLastSearchCityIdUseCase(get()) }
}

val viewModelsModule = module {
    viewModel { WeatherSearchViewModel(get(), get(), get()) }
}
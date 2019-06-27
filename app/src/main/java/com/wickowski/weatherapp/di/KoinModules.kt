package com.wickowski.weatherapp.di

import com.wickowski.weatherapp.domain.search_history.GetLastSearchCityIdUseCase
import com.wickowski.weatherapp.domain.current_weather.GetCurrentWeatherUseCase
import com.wickowski.weatherapp.domain.error.MapErrorUseCase
import com.wickowski.weatherapp.domain.search_history.SaveLastSearchCityIdUseCase
import com.wickowski.weatherapp.presentation.city_weather_details.CityWeatherDetailsViewModel
import com.wickowski.weatherapp.presentation.search.WeatherSearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single { createWeatherApiInstance(androidContext()) }
    single { createSearchHistoryProvider(androidContext()) }
}

val useCasesModule = module {
    single { GetCurrentWeatherUseCase(get()) }
    single { GetLastSearchCityIdUseCase(get()) }
    single { SaveLastSearchCityIdUseCase(get()) }
    single { MapErrorUseCase() }
}

val viewModelsModule = module {
    viewModel { WeatherSearchViewModel(get(), get(), get(), get()) }
    viewModel { (cityId: String) -> CityWeatherDetailsViewModel(cityId, get(), get()) }
}
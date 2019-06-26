package com.wickowski.weatherapp.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wickowski.weatherapp.domain.search_history.GetLastSearchCityIdUseCase
import com.wickowski.weatherapp.domain.current_weather.GetCurrentWeatherUseCase
import com.wickowski.weatherapp.domain.search_history.SaveLastSearchCityIdUseCase
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WeatherSearchViewModel(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getLastSearchCityIdUseCase: GetLastSearchCityIdUseCase,
    private val saveLastSearchCityIdUseCase: SaveLastSearchCityIdUseCase
) : ViewModel() {

    val weatherSearchState = MutableLiveData<WeatherState>()
    val lastSearchState = MutableLiveData<LastSearchState>()
    private var searchDisposable: Disposable? = null
    private var lastSearchDisposable: Disposable? = null

    fun loadDataForCityName(cityName: String) {
        searchDisposable?.dispose()
        searchDisposable = getCurrentWeatherUseCase.executeForCityName(cityName)
            .doOnSubscribe { weatherSearchState.postValue(WeatherState.Loading) }
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                weatherSearchState.postValue(WeatherState.Success(it))
                weatherSearchState.postValue(WeatherState.Idle)
                saveLastSearchCityIdUseCase.execute(it.cityId)
            }.subscribe({ }, { weatherSearchState.postValue(WeatherState.Error(it.message ?: "")) })
    }

    fun loadDataForLocation(latitude: Double, longitude: Double) {
        searchDisposable?.dispose()
        searchDisposable = getCurrentWeatherUseCase.executeForLocation(latitude, longitude)
            .doOnSubscribe { weatherSearchState.postValue(WeatherState.Loading) }
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                weatherSearchState.postValue(WeatherState.Success(it))
                weatherSearchState.postValue(WeatherState.Idle)
                saveLastSearchCityIdUseCase.execute(it.cityId)
            }
            .subscribe({ }, { weatherSearchState.postValue(WeatherState.Error(it.message ?: "")) })
    }

    fun loadLastCityForecast() {
        lastSearchDisposable = getLastSearchCityIdUseCase.execute()
            .subscribeOn(Schedulers.io())
            .flatMapMaybe {
                if (it.isNotEmpty()) {
                    getCurrentWeatherUseCase.executeForCityId(it).toMaybe()
                } else {
                    lastSearchState.postValue(LastSearchState.EmptyLastSearchForecast)
                    Maybe.empty()
                }
            }
            .subscribe(
                { lastSearchState.postValue(LastSearchState.LastSearchForecast(it)) },
                { lastSearchState.postValue(LastSearchState.LastSearchForecastError(it.message ?: "")) }
            )
    }

    override fun onCleared() {
        super.onCleared()
        searchDisposable?.dispose()
        lastSearchDisposable?.dispose()
    }

    sealed class WeatherState {
        object Loading : WeatherState()
        data class Error(val message: String) : WeatherState()
        data class Success(val currentWeather: CityCurrentWeather) : WeatherState()
        object Idle : WeatherState()
    }

    sealed class LastSearchState {
        object EmptyLastSearchForecast : LastSearchState()
        data class LastSearchForecast(val currentWeather: CityCurrentWeather) : LastSearchState()
        data class LastSearchForecastError(val message: String) : LastSearchState()
    }

}
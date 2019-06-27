package com.wickowski.weatherapp.presentation.search

import androidx.annotation.StringRes
import androidx.lifecycle.*
import com.hadilq.liveevent.LiveEvent
import com.wickowski.weatherapp.domain.search_history.GetLastSearchCityIdUseCase
import com.wickowski.weatherapp.domain.current_weather.GetCurrentWeatherUseCase
import com.wickowski.weatherapp.domain.error.MapErrorUseCase
import com.wickowski.weatherapp.domain.search_history.SaveLastSearchCityIdUseCase
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WeatherSearchViewModel(
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val getLastSearchCityIdUseCase: GetLastSearchCityIdUseCase,
    private val saveLastSearchCityIdUseCase: SaveLastSearchCityIdUseCase,
    private val mapErrorUseCase: MapErrorUseCase
) : ViewModel() {

    val weatherSearchState = MutableLiveData<WeatherSearchState>()
    val lastSearchState = MutableLiveData<CityCurrentWeather>()
    val searchResultEvent = LiveEvent<CityCurrentWeather?>()

    private var searchDisposable: Disposable? = null
    private var lastSearchDisposable: Disposable? = null

    fun loadDataForCityName(cityName: String) {
        weatherSearchState.postValue(WeatherSearchState.Loading)
        searchDisposable?.dispose()
        searchDisposable = getCurrentWeatherUseCase.executeForCityName(cityName)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                weatherSearchState.postValue(WeatherSearchState.Idle)
                searchResultEvent.postValue(it)
                saveLastSearchCityIdUseCase.execute(it.cityId)
            }
            .subscribe({}, ::handleWeatherSearchError)
    }

    fun loadDataForLocation(latitude: Double, longitude: Double) {
        weatherSearchState.postValue(WeatherSearchState.Loading)
        searchDisposable?.dispose()
        searchDisposable = getCurrentWeatherUseCase.executeForLocation(latitude, longitude)
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                weatherSearchState.postValue(WeatherSearchState.Idle)
                searchResultEvent.postValue(it)
                saveLastSearchCityIdUseCase.execute(it.cityId)
            }
            .subscribe({}, ::handleWeatherSearchError)
    }

    fun loadLastCityForecast() {
        lastSearchDisposable = getLastSearchCityIdUseCase.execute()
            .subscribeOn(Schedulers.io())
            .flatMapMaybe {
                if (it.isNotEmpty()) getCurrentWeatherUseCase.executeForCityId(it).toMaybe()
                else Maybe.empty()
            }
            .subscribe(
                { lastSearchState.postValue(it) },
                { it.printStackTrace() }
            )
    }

    private fun handleWeatherSearchError(throwable: Throwable) {
        val apiError = mapErrorUseCase.execute(throwable)
        weatherSearchState.postValue(WeatherSearchState.Error(apiError.messageStringRes))
    }

    override fun onCleared() {
        super.onCleared()
        searchDisposable?.dispose()
        lastSearchDisposable?.dispose()
    }

    sealed class WeatherSearchState {
        object Loading : WeatherSearchState()
        data class Error(@StringRes val messageRes: Int) : WeatherSearchState()
        object Idle : WeatherSearchState()
    }

}
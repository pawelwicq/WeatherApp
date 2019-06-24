package com.wickowski.weatherapp.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wickowski.weatherapp.domain.weather.GetLastSearchCityIdUseCase
import com.wickowski.weatherapp.domain.weather.GetWeatherForecastUseCase
import com.wickowski.weatherapp.domain.weather.SaveLastSearchCityIdUseCase
import com.wickowski.weatherapp.repository.net.WeatherForecast
import io.reactivex.Maybe
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WeatherSearchViewModel(
    private val getWeatherForecastUseCase: GetWeatherForecastUseCase,
    private val getLastSearchCityIdUseCase: GetLastSearchCityIdUseCase,
    private val saveLastSearchCityIdUseCase: SaveLastSearchCityIdUseCase
) : ViewModel() {

    val weatherSearchState = MutableLiveData<WeatherState>()
    val lastSearchState = MutableLiveData<LastSearchState>()
    private var searchDisposable: Disposable? = null
    private var lastSearchDisposable: Disposable? = null

    fun loadDataForCityName(cityName: String) {
        searchDisposable?.dispose()
        searchDisposable = getWeatherForecastUseCase.executeForCityName(cityName)
            .doOnSubscribe { weatherSearchState.postValue(WeatherState.Loading) }
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                weatherSearchState.postValue(WeatherState.Success(it))
                saveLastSearchCityIdUseCase.execute(it.cityId)
            }.subscribe({ }, { weatherSearchState.postValue(WeatherState.Error(it.message ?: "")) })
    }

    fun loadDataForLocation(latitude: Double, longitude: Double) {
        searchDisposable?.dispose()
        searchDisposable = getWeatherForecastUseCase.executeForLocation(latitude, longitude)
            .doOnSubscribe { weatherSearchState.postValue(WeatherState.Loading) }
            .subscribeOn(Schedulers.io())
            .flatMapCompletable {
                weatherSearchState.postValue(WeatherState.Success(it))
                saveLastSearchCityIdUseCase.execute(it.cityId)
            }
            .subscribe({ }, { weatherSearchState.postValue(WeatherState.Error(it.message ?: "")) })
    }

    fun loadLastCityForecast() {
        lastSearchDisposable = getLastSearchCityIdUseCase.execute()
            .subscribeOn(Schedulers.io())
            .flatMapMaybe {
                if (it.isNotEmpty()) getWeatherForecastUseCase.executeForCityId(it).toMaybe()
                else Maybe.empty()
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
        data class Success(val weatherForecast: WeatherForecast) : WeatherState()
    }

    sealed class LastSearchState {
        data class LastSearchForecast(val weatherForecast: WeatherForecast) : LastSearchState()
        data class LastSearchForecastError(val message: String) : LastSearchState()
    }

}
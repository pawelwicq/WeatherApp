package com.wickowski.weatherapp.presentation.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wickowski.weatherapp.domain.weather.GetWeatherForecastUseCase
import com.wickowski.weatherapp.repository.net.WeatherForecast
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class WeatherSearchViewModel(private val getWeatherForecastUseCase: GetWeatherForecastUseCase): ViewModel() {

    val state = MutableLiveData<WeatherState>()
    private var disposable: Disposable? = null

    fun loadDataForCity(cityName: String) {
        disposable?.dispose()
        disposable = getWeatherForecastUseCase.executeForCityName(cityName)
            .doOnSubscribe { state.postValue(WeatherState.Loading) }
            .subscribeOn(Schedulers.io())
            .subscribe({ state.postValue(
                WeatherState.Success(
                    it
                )
            ) }, { state.postValue(
                WeatherState.Error(
                    it.message ?: ""
                )
            ) })
    }

    fun loadDataForLocation(latitude: Double, longitude: Double) {
        disposable?.dispose()
        disposable = getWeatherForecastUseCase.executeForLocation(latitude, longitude)
            .doOnSubscribe { state.postValue(WeatherState.Loading) }
            .subscribeOn(Schedulers.io())
            .subscribe({ state.postValue(
                WeatherState.Success(
                    it
                )
            ) }, { state.postValue(
                WeatherState.Error(
                    it.message ?: ""
                )
            ) })
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    sealed class WeatherState {
        object Loading: WeatherState()
        data class Error(val message: String): WeatherState()
        data class Success(val weatherForecast: WeatherForecast): WeatherState()
    }

}
package com.wickowski.weatherapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wickowski.weatherapp.domain.weather.GetWeatherUseCase
import com.wickowski.weatherapp.repository.net.Weather
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class MainViewModel(private val getWeatherUseCase: GetWeatherUseCase): ViewModel() {

    val state = MutableLiveData<WeatherState>()
    private var disposable: Disposable? = null

    fun loadDataForCity(cityName: String) {
        disposable = getWeatherUseCase.execute(cityName)
            .subscribeOn(Schedulers.io())
            .subscribe({ state.postValue(WeatherState.Success(it)) }, { state.postValue(WeatherState.Error(it.message ?: "")) })
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    sealed class WeatherState {
        object Loading: WeatherState()
        data class Error(val message: String): WeatherState()
        data class Success(val weather: Weather): WeatherState()
    }

}
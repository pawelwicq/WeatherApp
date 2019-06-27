package com.wickowski.weatherapp.presentation.city_weather_details

import androidx.annotation.StringRes
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wickowski.weatherapp.domain.current_weather.GetCurrentWeatherUseCase
import com.wickowski.weatherapp.domain.error.MapErrorUseCase
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import com.wickowski.weatherapp.presentation.city_weather_details.CityWeatherDetailsViewModel.CityWeatherDetailsState.*
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CityWeatherDetailsViewModel(
    private val cityId: String,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase,
    private val mapErrorUseCase: MapErrorUseCase
) : ViewModel() {

    val cityWeatherDetailsState = MutableLiveData<CityWeatherDetailsState>()
    private var disposable: Disposable? = null

    fun loadCityWeatherDetails() {
        disposable?.dispose()
        disposable = getCurrentWeatherUseCase.executeForCityId(cityId)
            .doOnSubscribe { cityWeatherDetailsState.postValue(Loading) }
            .subscribeOn(Schedulers.io())
            .subscribe({ cityWeatherDetailsState.postValue(LoadingSuccess(it)) }, ::handleError)
    }

    private fun handleError(throwable: Throwable) {
        val error = mapErrorUseCase.execute(throwable)
        cityWeatherDetailsState.postValue(Error(error.messageStringRes))
    }

    override fun onCleared() {
        super.onCleared()
        disposable?.dispose()
    }

    sealed class CityWeatherDetailsState {
        object Loading : CityWeatherDetailsState()
        data class LoadingSuccess(val currentWeather: CityCurrentWeather) : CityWeatherDetailsState()
        data class Error(@StringRes val messageRes: Int) : CityWeatherDetailsState()
    }

}
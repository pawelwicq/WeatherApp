package com.wickowski.weatherapp.presentation.city_weather_details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wickowski.weatherapp.domain.current_weather.GetCurrentWeatherUseCase
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class CityWeatherDetailsViewModel(
    private val cityId: String,
    private val getCurrentWeatherUseCase: GetCurrentWeatherUseCase
) : ViewModel() {

    val cityWeatherDetailsState = MutableLiveData<CityWeatherDetailsState>()
    private var disposable: Disposable? = null

    fun loadCityWeatherDetails() {
        disposable = getCurrentWeatherUseCase.executeForCityId(cityId)
            .doOnSubscribe { cityWeatherDetailsState.postValue(CityWeatherDetailsState.Loading) }
            .subscribeOn(Schedulers.io())
            .subscribe({
                cityWeatherDetailsState.postValue(CityWeatherDetailsState.Success(it))
            } , {
                cityWeatherDetailsState.postValue(CityWeatherDetailsState.Error(it.message ?: ""))
            })
    }


    sealed class CityWeatherDetailsState {
        object Loading: CityWeatherDetailsState()
        data class Success(val currentWeather: CityCurrentWeather): CityWeatherDetailsState()
        data class Error(val message: String): CityWeatherDetailsState()
    }

}
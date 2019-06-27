package com.wickowski.weatherapp.presentation.city_weather_details

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.domain.current_weather.GetCurrentWeatherUseCase
import com.wickowski.weatherapp.domain.error.MapErrorUseCase
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import com.wickowski.weatherapp.presentation.city_weather_details.CityWeatherDetailsViewModel.CityWeatherDetailsState
import com.wickowski.weatherapp.presentation.city_weather_details.CityWeatherDetailsViewModel.CityWeatherDetailsState.*
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class CityWeatherDetailsViewModelTest {

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    private val currentWeather = CityCurrentWeather(
        cityId = "1234",
        cityName = "test name",
        temperature = 28,
        windSpeed = 1.5,
        airHumidity = 68,
        conditionStringRes = R.string.thunderstorm,
        pressure = 1044,
        weatherIcon = "01d.json"
    )

    private val mockGetCurrentWeatherUseCase = mock<GetCurrentWeatherUseCase> {
        on { executeForCityId(any()) } doReturn Single.just(currentWeather)
    }

    private val mockErrorGetCurrentWeatherUseCase = mock<GetCurrentWeatherUseCase> {
        on { executeForCityId(any()) } doReturn Single.error(Exception("Exception message"))
    }

    @Before
    fun makeSchedulersSynchronous() {
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.from { command -> command.run() } }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.from { command -> command.run() } }
    }

    @Test
    fun `live data responds to loading current weather details from repository`() {
        // given
        val viewModel = CityWeatherDetailsViewModel(
            "anyId",
            mockGetCurrentWeatherUseCase,
            MapErrorUseCase()
        )
        val observer = mock<Observer<CityWeatherDetailsState>>()
        viewModel.cityWeatherDetailsState.observeForever(observer)

        //when
        viewModel.loadCityWeatherDetails()

        // then
        verify(observer).onChanged(isA<Loading>())
        verify(observer).onChanged(isA<LoadingSuccess>())
    }

    @Test
    fun `live data responds to error when loading current weather details from repository`() {
        // given
        val viewModel = CityWeatherDetailsViewModel(
            "anyId",
            mockErrorGetCurrentWeatherUseCase,
            MapErrorUseCase()
        )
        val observer = mock<Observer<CityWeatherDetailsState>>()
        viewModel.cityWeatherDetailsState.observeForever(observer)

        //when
        viewModel.loadCityWeatherDetails()

        // then
        verify(observer).onChanged(isA<Loading>())
        verify(observer).onChanged(isA<Error>())
    }

    @After
    fun clearMocks() {
        Mockito.framework().clearInlineMocks()
    }

}
package com.wickowski.weatherapp.presentation.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.nhaarman.mockitokotlin2.*
import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.domain.current_weather.GetCurrentWeatherUseCase
import com.wickowski.weatherapp.domain.error.MapErrorUseCase
import com.wickowski.weatherapp.domain.search_history.GetLastSearchCityIdUseCase
import com.wickowski.weatherapp.domain.search_history.SaveLastSearchCityIdUseCase
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

class WeatherSearchViewModelTest {

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

    private val lastSearchWeather = CityCurrentWeather(
        cityId = "333",
        cityName = "Last searched city",
        temperature = 28,
        windSpeed = 1.5,
        airHumidity = 68,
        conditionStringRes = R.string.clear,
        pressure = 1044,
        weatherIcon = "01d.json"
    )

    val mockGetCurrentWeatherUseCase = mock<GetCurrentWeatherUseCase> {
        on { executeForCityName(any()) } doReturn Single.just(currentWeather)
        on { executeForLocation(any(), any()) } doReturn Single.just(currentWeather)
        on { executeForCityId(any()) } doReturn Single.just(currentWeather)
    }

    val mockSaveLastSearchCityIdUseCase = mock<SaveLastSearchCityIdUseCase> {
        on { execute(any()) } doReturn Completable.complete()
    }


    val mockGetLastSearchCityIdUseCase = mock<GetLastSearchCityIdUseCase> {
        on { execute() } doReturn Single.just("333")
    }


    val viewModel = WeatherSearchViewModel(
        mockGetCurrentWeatherUseCase,
        mockGetLastSearchCityIdUseCase,
        mockSaveLastSearchCityIdUseCase,
        MapErrorUseCase()
    )

    @Before
    fun makeSchedulersSynchronous() {
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.from { command -> command.run() } }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.from { command -> command.run() } }
    }

    @Test
    fun `when load city weather called then viewmodel asks use case for it`() {
        viewModel.loadDataForCityName("any name")
        verify(mockGetCurrentWeatherUseCase, times(1)).executeForCityName(any())
    }

    @Test
    fun `live data responds to current weather loaded from repository`() {
//        val observer = mock<Observer<OrderDetailsViewData>>()
//        viewModel.observeLastSearchState()
//
//        orderDetailsViewModel.loadOrder(1L)
//
//        verify(observer).onChanged(isA<OrderDetailsLoading>())
//        verify(observer).onChanged(isA<OrderDetails>())
    }

    @After
    fun clearMocks() {
        Mockito.framework().clearInlineMocks()
    }
}
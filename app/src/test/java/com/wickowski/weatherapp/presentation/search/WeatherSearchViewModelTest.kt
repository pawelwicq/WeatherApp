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
import com.wickowski.weatherapp.presentation.search.WeatherSearchViewModel.WeatherSearchState.*
import com.wickowski.weatherapp.presentation.search.WeatherSearchViewModel.WeatherSearchState
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

    private val mockGetCurrentWeatherUseCase = mock<GetCurrentWeatherUseCase> {
        on { executeForCityName(any()) } doReturn Single.just(currentWeather)
        on { executeForLocation(any(), any()) } doReturn Single.just(currentWeather)
        on { executeForCityId(any()) } doReturn Single.just(currentWeather)
    }

    private val mockErrorGetCurrentWeatherUseCase = mock<GetCurrentWeatherUseCase> {
        on { executeForCityName(any()) } doReturn Single.error(Exception("Exception message"))
        on { executeForLocation(any(), any()) } doReturn Single.error(Exception("Exception message"))
        on { executeForCityId(any()) } doReturn Single.error(Exception("Exception message"))
    }

    private val mockSaveLastSearchCityIdUseCase = mock<SaveLastSearchCityIdUseCase> {
        on { execute(any()) } doReturn Completable.complete()
    }


    private val mockGetLastSearchCityIdUseCase = mock<GetLastSearchCityIdUseCase> {
        on { execute() } doReturn Single.just("333")
    }

    private val mockEmptyGetLastSearchCityIdUseCase = mock<GetLastSearchCityIdUseCase> {
        on { execute() } doReturn Single.just("")
    }

    @Before
    fun makeSchedulersSynchronous() {
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.from { command -> command.run() } }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.from { command -> command.run() } }
    }

    @Test
    fun `live data responds to current weather loaded by city name from repository`() {
        // given
        val viewModel = WeatherSearchViewModel(
            mockGetCurrentWeatherUseCase,
            mockGetLastSearchCityIdUseCase,
            mockSaveLastSearchCityIdUseCase,
            MapErrorUseCase()
        )
        val observer = mock<Observer<WeatherSearchState>>()
        viewModel.weatherSearchState.observeForever(observer)

        //when
        viewModel.loadDataForCityName("any name")

        // then
        verify(observer).onChanged(isA<Loading>())
        verify(observer).onChanged(isA<Idle>())
    }

    @Test
    fun `live data responds to error on current weather loading by city name from repository`() {
        // given
        val viewModel = WeatherSearchViewModel(
            mockErrorGetCurrentWeatherUseCase,
            mockGetLastSearchCityIdUseCase,
            mockSaveLastSearchCityIdUseCase,
            MapErrorUseCase()
        )
        val observer = mock<Observer<WeatherSearchState>>()
        viewModel.weatherSearchState.observeForever(observer)

        //when
        viewModel.loadDataForCityName("any name")

        // then
        verify(observer).onChanged(isA<Loading>())
        verify(observer).onChanged(isA<Error>())
    }

    @Test
    fun `live data responds to current weather loaded by location from repository`() {
        // given
        val viewModel = WeatherSearchViewModel(
            mockGetCurrentWeatherUseCase,
            mockGetLastSearchCityIdUseCase,
            mockSaveLastSearchCityIdUseCase,
            MapErrorUseCase()
        )
        val observer = mock<Observer<WeatherSearchState>>()
        viewModel.weatherSearchState.observeForever(observer)

        //when
        viewModel.loadDataForLocation(1.3, 2.3)

        // then
        verify(observer).onChanged(isA<Loading>())
        verify(observer).onChanged(isA<Idle>())
    }

    @Test
    fun `live data responds to error on current weather loading by location from repository`() {
        // given
        val viewModel = WeatherSearchViewModel(
            mockErrorGetCurrentWeatherUseCase,
            mockGetLastSearchCityIdUseCase,
            mockSaveLastSearchCityIdUseCase,
            MapErrorUseCase()
        )
        val observer = mock<Observer<WeatherSearchState>>()
        viewModel.weatherSearchState.observeForever(observer)

        //when
        viewModel.loadDataForLocation(1.5, 2.3)

        // then
        verify(observer).onChanged(isA<Loading>())
        verify(observer).onChanged(isA<Error>())
    }

    @Test
    fun `live data responds to last search weather loaded by city id from repository`() {
        // given
        val viewModel = WeatherSearchViewModel(
            mockGetCurrentWeatherUseCase,
            mockGetLastSearchCityIdUseCase,
            mockSaveLastSearchCityIdUseCase,
            MapErrorUseCase()
        )
        val observer = mock<Observer<CityCurrentWeather>>()
        viewModel.lastSearchState.observeForever(observer)

        //when
        viewModel.loadLastCityForecast()

        // then
        verify(observer, times(1)).onChanged(currentWeather)
    }

    @Test
    fun `live data does not load last search forecast when none searched before`() {
        // given
        val viewModel = WeatherSearchViewModel(
            mockGetCurrentWeatherUseCase,
            mockEmptyGetLastSearchCityIdUseCase,
            mockSaveLastSearchCityIdUseCase,
            MapErrorUseCase()
        )
        val observer = mock<Observer<CityCurrentWeather>>()
        viewModel.lastSearchState.observeForever(observer)

        //when
        viewModel.loadLastCityForecast()

        // then
        verify(observer, times(0)).onChanged(any())
    }

    @Test
    fun `live data does not call onChanged method when use case throws error`() {
        // given
        val viewModel = WeatherSearchViewModel(
            mockErrorGetCurrentWeatherUseCase,
            mockGetLastSearchCityIdUseCase,
            mockSaveLastSearchCityIdUseCase,
            MapErrorUseCase()
        )
        val observer = mock<Observer<CityCurrentWeather>>()
        viewModel.lastSearchState.observeForever(observer)

        //when
        viewModel.loadLastCityForecast()

        // then
        verify(observer, times(0)).onChanged(any())
    }

    @After
    fun clearMocks() {
        Mockito.framework().clearInlineMocks()
    }
}
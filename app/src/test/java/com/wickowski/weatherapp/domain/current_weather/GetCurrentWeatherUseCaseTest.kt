package com.wickowski.weatherapp.domain.current_weather

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import com.wickowski.weatherapp.repository.net.*
import io.reactivex.Single
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class GetCurrentWeatherUseCaseTest {

    private val apiEntityWeatherForecast = WeatherForecast(
        cityId = "1234",
        cityName = "test name",
        weather = listOf(Weather("800", WeatherCondition.THUNDERSTORM, "01d", "Big thunderstorm")),
        main = MainForecastInfo(28.3, 68, 1044),
        wind = WindInfo(1.5)
    )

    private val viewEntityWeather = CityCurrentWeather(
        cityId = "1234",
        cityName = "test name",
        temperature = 28,
        windSpeed = 1.5,
        airHumidity = 68,
        conditionStringRes = R.string.thunderstorm,
        pressure = 1044,
        weatherIcon = "01d.json"
    )

    private val mockApi = mock<RemoteDataSource> {
        on { getWeatherForecast(any()) } doReturn Single.just(apiEntityWeatherForecast)
    }

    @Rule
    @JvmField
    val rule = InstantTaskExecutorRule()

    @Before
    fun makeSchedulersSynchronous() {
        RxJavaPlugins.setInitIoSchedulerHandler { Schedulers.from { command -> command.run() } }
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { Schedulers.from { command -> command.run() } }
    }

    @Test
    fun `use case maps correctly api entity to current weather view entity when querying with city id`() {
        // given
        val getCurrentWeatherUseCase = GetCurrentWeatherUseCase(mockApi)

        //when
        val result = getCurrentWeatherUseCase.executeForCityId("1234").blockingGet()

        // then)
        assertEquals(result, viewEntityWeather)
    }

    @Test
    fun `use case maps correctly api entity to current weather view entity when querying with city name`() {
        // given
        val getCurrentWeatherUseCase = GetCurrentWeatherUseCase(mockApi)

        //when
        val result = getCurrentWeatherUseCase.executeForCityName("test name").blockingGet()

        // then)
        assertEquals(result, viewEntityWeather)
    }

    @Test
    fun `use case maps correctly api entity to current weather view entity when querying with location`() {
        // given
        val getCurrentWeatherUseCase = GetCurrentWeatherUseCase(mockApi)

        //when
        val result = getCurrentWeatherUseCase.executeForLocation(11.3, 23.2).blockingGet()

        // then)
        assertEquals(result, viewEntityWeather)
    }

}
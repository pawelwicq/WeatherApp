package com.wickowski.weatherapp.presentation.city_weather_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import java.lang.IllegalStateException
import com.wickowski.weatherapp.presentation.city_weather_details.CityWeatherDetailsViewModel.*
import com.wickowski.weatherapp.utils.showToast
import kotlinx.android.synthetic.main.fragment_city_weather_details.*
import kotlinx.android.synthetic.main.layout_current_weather_details_card.*

private const val CITY_ID_BUNDLE_EXTRA = "CITY_ID"

class CityWeatherDetailsFragment : Fragment() {

    companion object {
        fun createBundle(cityId: String) = Bundle().apply {
            putString(CITY_ID_BUNDLE_EXTRA, cityId)
        }
    }

    private lateinit var cityId: String
    private val viewModel: CityWeatherDetailsViewModel by viewModel { parametersOf(cityId) }

    private val weatherStateObserver = Observer<CityWeatherDetailsState> { handleWeatherState(it) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_city_weather_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cityId = arguments?.getString(CITY_ID_BUNDLE_EXTRA)
            ?: throw IllegalStateException("City ID must be provided to this fragment")
        viewModel.loadCityWeatherDetails()
        closeBtn.setOnClickListener { findNavController().navigateUp() }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cityWeatherDetailsState.observe(viewLifecycleOwner, weatherStateObserver)
    }


    private fun handleWeatherState(state: CityWeatherDetailsState) = when (state) {
        CityWeatherDetailsState.Loading -> showToast("Loading")
        is CityWeatherDetailsState.Success -> updateCurrentWeatherData(state.currentWeather)
        is CityWeatherDetailsState.Error -> showToast("Err")
    }

    private fun updateCurrentWeatherData(currentWeather: CityCurrentWeather) = with(currentWeather) {
        city.text = cityName
        temperatureValue.text = getString(R.string.celsius_temperature, temperature)
        currentWeatherIcon.setAnimation(weatherIcon)
        pressureValue.text = getString(R.string.h_pascal_pressure, pressure)
        currentWeatherDescription.text = getString(conditionStringRes).capitalize()
        humidityValue.text = getString(R.string.humidity_percentage, airHumidity)
        windSpeedValue.text = getString(R.string.wind_speed_meters_per_second, windSpeed)
    }

}
package com.wickowski.weatherapp.presentation.city_weather_details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.wickowski.weatherapp.R
import kotlinx.android.synthetic.main.fragment_city_weather_details.*
import java.lang.IllegalStateException

private const val CITY_ID_BUNDLE_EXTRA = "CITY_ID"

class CityWeatherDetailsFragment : Fragment() {

    companion object {
        fun newInstance(cityId: String) = CityWeatherDetailsFragment().apply {
            arguments = Bundle().apply {
                putString(CITY_ID_BUNDLE_EXTRA, cityId)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_city_weather_details, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cityName.text = arguments?.getString(CITY_ID_BUNDLE_EXTRA)
            ?: throw IllegalStateException("City ID must be provided to this fragment")
    }
}
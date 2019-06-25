package com.wickowski.weatherapp.presentation.search

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.airbnb.lottie.LottieDrawable
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.utils.LocationUtils
import com.wickowski.weatherapp.utils.showToast
import com.wickowski.weatherapp.utils.turnGPSOn
import kotlinx.android.synthetic.main.fragment_weather_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.wickowski.weatherapp.presentation.search.WeatherSearchViewModel.WeatherState.*
import com.wickowski.weatherapp.presentation.search.WeatherSearchViewModel.LastSearchState.*
import com.wickowski.weatherapp.presentation.search.WeatherSearchViewModel.LastSearchState
import com.wickowski.weatherapp.utils.getText
import kotlinx.android.synthetic.main.layout_last_search_card_content.*
import java.lang.IllegalStateException
import kotlin.math.roundToInt


private const val GPS_REQUEST_CODE = 1000

class WeatherSearchFragment : Fragment(), GoogleApiClient.ConnectionCallbacks,
    GoogleApiClient.OnConnectionFailedListener {

    companion object {
        fun newInstance() = WeatherSearchFragment()
    }

    private val viewModel: WeatherSearchViewModel by viewModel()
    private val stateObserver = Observer<WeatherSearchViewModel.WeatherState> { handleWeatherState(it) }
    private val lastSearchObserver = Observer<LastSearchState> { handleLastSearch(it) }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationRes: LocationResult?) {
            locationRes?.lastLocation?.let {
                viewModel.loadDataForLocation(it.latitude, it.longitude)
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    private lateinit var googleApiClient: GoogleApiClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_weather_search, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initGoogleApiClient()
        initView()
    }

    override fun onResume() {
        super.onResume()
        viewModel.weatherSearchState.observe(viewLifecycleOwner, stateObserver)
        viewModel.lastSearchState.observe(viewLifecycleOwner, lastSearchObserver)
        viewModel.loadLastCityForecast()
    }

    private fun initView() {
        searchBtn.setOnClickListener { search(searchInput.getText()) }
        getLocationBtn.setOnClickListener {
            getWeatherForCurrentLocation()
        }
    }

    private fun initGoogleApiClient() = context?.let {
        googleApiClient = GoogleApiClient.Builder(it)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        googleApiClient.connect()
    } ?: throw IllegalStateException("Context was null")

    private fun getWeatherForCurrentLocation() = RxPermissions(this).request(
        Manifest.permission.ACCESS_FINE_LOCATION
    ).subscribe { granted ->
        if (granted) turnGPSOn(GPS_REQUEST_CODE) { isOn -> if(isOn) getLocation() }
        else showToast(getString(R.string.missing_permissions_error))
    }

    @SuppressLint("MissingPermission")  // permissions are already checked by rxpermissions
    private fun getLocation() = fusedLocationClient.requestLocationUpdates(LocationUtils.buildLocationRequest(), locationCallback, null)

    private fun search(text: String) {
        if (text.isNotEmpty()) viewModel.loadDataForCityName(text)
    }

    private fun handleWeatherState(state: WeatherSearchViewModel.WeatherState) = when (state) {
        Loading ->{
            showToast("LOADING")
        }
        is Success -> { showToast("SUCCESS") }
        is Error ->  {
            showToast("ERR")
        }
    }

    private fun handleLastSearch(state: LastSearchState) = when(state) {
        EmptyLastSearchForecast -> lastSearchCard.visibility = View.INVISIBLE
        is LastSearchForecast -> with(state.weatherForecast) {
            lastSearchCard.visibility = View.VISIBLE
            lastSearchCityName.text = cityName
            lastSearchTemperature.text = getString(R.string.temperature_string_pattern, main.temp.roundToInt())
            lastSearchWeatherIcon.setAnimation("${weather[0].icon}.json")
        }
        is LastSearchForecastError -> lastSearchCard.visibility = View.INVISIBLE
    }

    override fun onConnected(bundle: Bundle?) {
        getLocationBtn.visibility = View.VISIBLE
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
    }

    override fun onConnectionSuspended(cause: Int) {
        googleApiClient.connect()
    }

    override fun onConnectionFailed(result: ConnectionResult) {
        getLocationBtn.visibility = View.INVISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GPS_REQUEST_CODE) {
            getLocation()
        }
    }

}
package com.wickowski.weatherapp.presentation.search

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.presentation.CityCurrentWeather
import com.wickowski.weatherapp.presentation.city_weather_details.CityWeatherDetailsFragment
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.wickowski.weatherapp.presentation.search.WeatherSearchViewModel.WeatherSearchState.*
import kotlinx.android.synthetic.main.layout_last_search_card_content.*
import java.lang.IllegalStateException
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.*
import com.wickowski.weatherapp.utils.*
import kotlinx.android.synthetic.main.layout_error_view.*
import kotlinx.android.synthetic.main.layout_error_view.view.*
import kotlinx.android.synthetic.main.layout_last_search_card_content.view.*
import kotlinx.android.synthetic.main.layout_progress_view.*
import kotlinx.android.synthetic.main.layout_weather_search.*


const val TURN_GPS_ON_REQUEST_CODE = 1000

class WeatherSearchFragment : Fragment(), ConnectionCallbacks, OnConnectionFailedListener {

    private val viewModel: WeatherSearchViewModel by viewModel()
    private val stateObserver = Observer<WeatherSearchViewModel.WeatherSearchState> { handleWeatherState(it) }
    private val searchResultObserver = Observer<CityCurrentWeather?> { handleSearchResult(it) }
    private val lastSearchObserver = Observer<CityCurrentWeather> { handleLastSearch(it) }

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationRes: LocationResult?) {
            locationRes?.locations?.getOrNull(0)?.let {
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
        showSearchView()
        initGoogleApiClient()
        initView()
        observeViewModelData()
    }

    private fun observeViewModelData() {
        viewModel.weatherSearchState.observe(viewLifecycleOwner, stateObserver)
        viewModel.lastSearchState.observe(viewLifecycleOwner, lastSearchObserver)
        viewModel.searchResultEvent.observe(viewLifecycleOwner, searchResultObserver)
    }

    override fun onResume() {
        super.onResume()
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
        if (granted) turnGPSOn(TURN_GPS_ON_REQUEST_CODE) { isOn -> if (isOn) getLocation() }
        else showToast(getString(R.string.missing_permissions_error))
    }

    @SuppressLint("MissingPermission")  // permissions are already checked by rxpermissions
    private fun getLocation() {
        showToast(getString(R.string.trying_to_obtain_location))
        fusedLocationClient.requestLocationUpdates(
            LocationUtils.buildLocationRequest(),
            locationCallback,
            null
        )
    }

    private fun search(text: String) = if (text.isNotEmpty()) {
        searchInput.editText?.error = null
        viewModel.loadDataForCityName(text.trim())
    } else {
        searchInput.editText?.error = getString(R.string.empty_city_name)
    }


    private fun handleWeatherState(state: WeatherSearchViewModel.WeatherSearchState) = when (state) {
        Loading -> showProgressView()
        Idle -> showSearchView()
        is Error -> showErrorView(state)
    }


    private fun showProgressView() {
        progressView.visible()
        weatherSearchView.gone()
        errorView.gone()
    }

    private fun showSearchView() {
        weatherSearchView.visible()
        progressView.gone()
        errorView.gone()
    }

    private fun showErrorView(state: Error) {
        weatherSearchView.gone()
        progressView.gone()
        errorView.visible()
        errorView.errorMessage.text = getString(state.messageRes)
        tryAgainBtn.setOnClickListener { showSearchView() }
    }

    private fun handleSearchResult(weather: CityCurrentWeather?) = weather?.cityId?.let {
        navigateToCityDetails(it)
    }

    private fun navigateToCityDetails(cityId: String) = view?.findNavController()?.navigate(
        R.id.openWeatherDetailsFragment,
        CityWeatherDetailsFragment.createBundle(cityId)
    )

    private fun handleLastSearch(lastSearchWeather: CityCurrentWeather) = fillLastSearchCardData(lastSearchWeather)

    private fun fillLastSearchCardData(currentWeather: CityCurrentWeather) = with(currentWeather) {
        lastSearchCityName.text = cityName
        lastSearchWeather.text = getString(conditionStringRes)
        lastSearchTemperature.text = getString(R.string.celsius_temperature, temperature)
        lastSearchCard.setOnClickListener { navigateToCityDetails(cityId) }
        if (!lastSearchCard.isVisible) {
            lastSearchCard.animate(R.anim.slide_in_bottom) {
                lastSearchCard?.visible()
                lastSearchCard?.lastSearchWeatherIcon?.setAnimation(weatherIcon)
            }
        }
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
        if (resultCode == Activity.RESULT_OK && requestCode == TURN_GPS_ON_REQUEST_CODE) {
            getLocation()
        }
    }

}
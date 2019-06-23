package com.wickowski.weatherapp.presentation

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.tbruyelle.rxpermissions2.RxPermissions
import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.presentation.MainViewModel.WeatherState
import com.wickowski.weatherapp.presentation.MainViewModel.WeatherState.*
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.view.View
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.location.*
import com.wickowski.weatherapp.utils.turnGPSOn
import com.google.android.gms.location.LocationServices
import com.google.android.gms.common.api.GoogleApiClient
import com.wickowski.weatherapp.utils.LocationUtils
import com.wickowski.weatherapp.utils.showToast

private const val GPS_REQUEST_CODE = 1000

class MainActivity : AppCompatActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private val viewModel: MainViewModel by viewModel()
    private val stateObserver = Observer<WeatherState> { handleWeatherState(it) }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initGoogleApiClient()
        searchBtn.setOnClickListener { search(searchEt.text.toString()) }
        getLocationBtn.setOnClickListener {
            getWeatherForCurrentLocation()
        }
    }

    private fun initGoogleApiClient() {
        googleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        googleApiClient.connect()
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.observe(this, stateObserver)
    }

    private fun getWeatherForCurrentLocation() = RxPermissions(this).request(
        Manifest.permission.ACCESS_FINE_LOCATION
    ).subscribe { granted ->
        if (granted) {
            turnGPSOn(GPS_REQUEST_CODE) { isOn -> getLocation() }
        } else {
            showToast("Missing permissions")
        }
    }

    @SuppressLint("MissingPermission")  // permissions are already checked by rxpermissions
    private fun getLocation() = fusedLocationClient.requestLocationUpdates(LocationUtils.buildLocationRequest(), locationCallback, null)

    private fun search(text: String) {
        if (text.isNotEmpty()) viewModel.loadDataForCity(text)
    }

    private fun handleWeatherState(state: WeatherState) = when (state) {
        Loading -> showToast("LOADING")
        is Success -> {
            with(state.weatherForecast) {
                logo.setAnimation("${weather[0].icon}.json")
            }
        }
        is Error -> showToast("ERR")
    }

    override fun onConnected(p0: Bundle?) {
        getLocationBtn.visibility = View.VISIBLE
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
    }

    override fun onConnectionSuspended(p0: Int) {
        googleApiClient.connect()
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
        getLocationBtn.visibility = View.INVISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == GPS_REQUEST_CODE) {
            getLocation()
        }
    }

}




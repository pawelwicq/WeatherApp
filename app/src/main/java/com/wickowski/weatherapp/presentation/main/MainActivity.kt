package com.wickowski.weatherapp.presentation.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.wickowski.weatherapp.R
import kotlinx.android.synthetic.main.activity_main.*
import android.content.Intent
import com.wickowski.weatherapp.presentation.search.TURN_GPS_ON_REQUEST_CODE


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onSupportNavigateUp() = findNavController(hostFragment.id).navigateUp()

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (checkIfWeatherSearchFragment() && requestCode == TURN_GPS_ON_REQUEST_CODE) {
            hostFragment.childFragmentManager.fragments
                .firstOrNull()?.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun checkIfWeatherSearchFragment() = findNavController(hostFragment.id)
        .currentDestination?.id == R.id.weatherSearchFragment
}




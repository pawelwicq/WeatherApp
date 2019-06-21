package com.wickowski.weatherapp.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.presentation.MainViewModel.WeatherState
import com.wickowski.weatherapp.presentation.MainViewModel.WeatherState.*
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModel()
    private val stateObserver = Observer<WeatherState> { handleWeatherState(it) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        searchBtn.setOnClickListener { search(searchEt.text.toString()) }
    }

    override fun onResume() {
        super.onResume()
        viewModel.state.observe(this, stateObserver)
    }

    private fun search(text: String) {
        if(text.isNotEmpty()) viewModel.loadDataForCity(text)
    }

    private fun handleWeatherState(state: WeatherState) = when(state) {
        Loading -> { Toast.makeText(this, "LOADING", Toast.LENGTH_SHORT).show() }
        is Success -> { Toast.makeText(this, "SUCCESS", Toast.LENGTH_SHORT).show() }
        is Error -> {Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show() }
    }


}




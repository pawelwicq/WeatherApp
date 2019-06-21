package com.wickowski.weatherapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.wickowski.weatherapp.R
import com.wickowski.weatherapp.repository.net.QueryBuilder
import com.wickowski.weatherapp.repository.net.RemoteDataSource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    val repository: RemoteDataSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        repository.queryForWeather(QueryBuilder().search("Jasło").build())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { Log.e("DD", it.id  + " "+ it.name) }, { Log.e("ERR", it.localizedMessage); it.printStackTrace() }
            )
    }
}

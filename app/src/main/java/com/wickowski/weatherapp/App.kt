package com.wickowski.weatherapp

import android.app.Application
import com.wickowski.weatherapp.di.dataModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App: Application() {

    private val koinModules = listOf(dataModule)

    override fun onCreate() {
        super.onCreate()
        setupKoin()
    }

    private fun setupKoin() = startKoin {
        androidContext(this@App)
        modules(koinModules)
    }

}
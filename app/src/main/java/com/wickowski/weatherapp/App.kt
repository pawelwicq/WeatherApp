package com.wickowski.weatherapp

import android.app.Application
import com.wickowski.weatherapp.di.dataModule
import com.wickowski.weatherapp.di.useCasesModule
import com.wickowski.weatherapp.di.viewModelsModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private val koinModules = listOf(dataModule, useCasesModule, viewModelsModule)

    override fun onCreate() {
        super.onCreate()
        setupKoin()
    }

    private fun setupKoin() = startKoin {
        androidContext(this@App)
        modules(koinModules)
    }

}
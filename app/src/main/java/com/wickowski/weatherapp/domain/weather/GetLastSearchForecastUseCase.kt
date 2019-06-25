package com.wickowski.weatherapp.domain.weather

import com.wickowski.weatherapp.repository.shared_prefs.SearchHistoryProvider
import io.reactivex.Single

class GetLastSearchCityIdUseCase(private val searchHistory: SearchHistoryProvider) {

    fun execute() = Single.just(searchHistory.lastSearchCityId)

}
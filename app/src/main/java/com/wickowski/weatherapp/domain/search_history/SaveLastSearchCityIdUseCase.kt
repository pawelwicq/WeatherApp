package com.wickowski.weatherapp.domain.search_history

import com.wickowski.weatherapp.repository.shared_prefs.SearchHistoryProvider
import io.reactivex.Completable

class SaveLastSearchCityIdUseCase(private val searchHistoryProvider: SearchHistoryProvider) {

    fun execute(cityId: String) = Completable.create {
        searchHistoryProvider.lastSearchCityId = cityId
        it.onComplete()
    }

}
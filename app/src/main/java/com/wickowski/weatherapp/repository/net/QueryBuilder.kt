package com.wickowski.weatherapp.repository.net


private const val SEARCH_QUERY_TEXT = "q"

class QueryBuilder {

    private val queryMap = HashMap<String, String>()

    fun searchWithCityName(city: String) = apply {
        queryMap[SEARCH_QUERY_TEXT] = city
    }

    fun build() = queryMap

}
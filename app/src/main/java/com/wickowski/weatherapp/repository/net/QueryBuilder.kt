package com.wickowski.weatherapp.repository.net


private const val SEARCH_QUERY_TEXT = "q"
private const val SEARCH_QUERY_LATITUDE = "lat"
private const val SEARCH_QUERY_LONGITUDE = "lon"

class QueryBuilder {

    private val queryMap = HashMap<String, String>()

    fun searchWithCityName(city: String) = apply {
        queryMap[SEARCH_QUERY_TEXT] = city
    }

    fun searchWithLocation(lat: Double, lon: Double) = apply {
        queryMap[SEARCH_QUERY_LATITUDE] = lat.toString()
        queryMap[SEARCH_QUERY_LONGITUDE] = lon.toString()
    }

    fun build() = queryMap

}
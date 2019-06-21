package com.wickowski.weatherapp.repository.net


private const val SEARCH = "q"

class QueryBuilder {

    private val queryMap = HashMap<String, String>()

    fun search(city: String): QueryBuilder {
        queryMap[SEARCH] = city
        return this
    }

    fun build() = queryMap

}
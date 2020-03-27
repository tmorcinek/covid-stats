package com.morcinek.covid.ui.summary

import retrofit2.http.GET

data class SummaryData(
    val Countries: List<SummaryCountry>,
    val Date: String
)

data class SummaryCountry(
    val Country: String,
    val CountrySlug: String,
    val NewConfirmed: Int,
    val NewDeaths: Int,
    val NewRecovered: Int,
    val TotalConfirmed: Int,
    val TotalDeaths: Int,
    val TotalRecovered: Int
)

interface SummaryApi {
    @GET("summary")
    suspend fun getData(): SummaryData
}
package com.morcinek.covid.ui.country

import retrofit2.http.GET
import retrofit2.http.Path
import java.util.*

data class DayData(
//    val Country: String,
//    val Status: String,
    val Date: Date,
    val Cases: Int
)

interface CountryApi {
    @GET("total/country/{slug}/status/{status}")
    suspend fun getData(@Path("slug") countrySlug: String, @Path("status") status: String): List<DayData>
}
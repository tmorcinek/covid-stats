package com.morcinek.covid.ui.countries

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import retrofit2.http.GET

data class SummaryData(
    val Countries: List<SummaryCountry>,
    val Date: String
)

@Parcelize
data class SummaryCountry(
    val Country: String,
    val Slug: String,
    val NewConfirmed: Int,
    val NewDeaths: Int,
    val NewRecovered: Int,
    val TotalConfirmed: Int,
    val TotalDeaths: Int,
    val TotalRecovered: Int
) : Parcelable {

    fun hasData() = TotalConfirmed > 100

    fun deathRate(): Float = TotalDeaths.toFloat() * 100 / TotalConfirmed
}

interface SummaryApi {
    @GET("summary")
    suspend fun getData(): SummaryData
}
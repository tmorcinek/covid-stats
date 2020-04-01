package com.morcinek.covid.ui.summary

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
) : Parcelable

interface SummaryApi {
    @GET("summary")
    suspend fun getData(): SummaryData
}
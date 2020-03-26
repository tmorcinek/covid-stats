package com.morcinek.covid.ui.home

import retrofit2.http.GET

data class HomeData(val Name: String, val Description: String, val Path: String, val Params : List<String>?)

interface HomeApi {
    @GET("/")
    suspend fun getData(): List<HomeData>
}
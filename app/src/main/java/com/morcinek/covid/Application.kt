package com.morcinek.covid

import android.app.Application
import com.google.gson.GsonBuilder
import com.morcinek.covid.ui.home.homeModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.scope.Scope
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(
                appModule,
//                navModule,
                homeModule
//                teamsModule, teamDetailsModule, createTeamModule, addPlayersModule,
//                createEventModule, eventDetailsModule,
//                playersModule, playerDetailsModule, createPlayerModule, playerStatsModule,
//                funinoModule, tournamentDetailsModule,
//                whichPlayersModule, howManyGamesModule, whatColorsModule
            )
        }
    }
}

val appModule = module {

    single<Retrofit> {
        Retrofit.Builder()
            .baseUrl("https://api.covid19api.com/")
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .build()
    }
}

inline fun <reified T> Scope.getApi(): T = get<Retrofit>().create(T::class.java)

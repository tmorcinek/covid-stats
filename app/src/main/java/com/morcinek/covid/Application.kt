package com.morcinek.covid

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.dsl.module

class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@Application)
            modules(
                appModule
//                navModule,
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

    //    single<SharedPreferences> { PreferenceManager.getDefaultSharedPreferences(androidApplication()) }
}
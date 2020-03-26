package com.morcinek.covid.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.morcinek.covid.core.extensions.startNewActivityFinishCurrent

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        startNewActivityFinishCurrent<NavActivity>()
    }
}
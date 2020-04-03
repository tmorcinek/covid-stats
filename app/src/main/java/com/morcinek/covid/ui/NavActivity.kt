package com.morcinek.covid.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.morcinek.covid.R
import kotlinx.android.synthetic.main.activity_main.*

class NavActivity : AppCompatActivity() {

    private val appBarConfiguration by lazy {
        AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_top),
            drawerLayout
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        findNavController(R.id.navHostFragment).let { navController ->
            setupActionBarWithNavController(navController, appBarConfiguration)
            navigationView.apply {
                setupWithNavController(navController)
            }
        }
    }

    override fun onSupportNavigateUp() = findNavController(R.id.navHostFragment).navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
}

fun Fragment.findNavController(): NavController = Navigation.findNavController(view!!)

fun Fragment.lazyNavController() = lazy { findNavController() }

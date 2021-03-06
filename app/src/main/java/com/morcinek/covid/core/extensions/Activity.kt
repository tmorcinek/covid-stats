package com.morcinek.covid.core.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri


inline fun <reified T : Activity> Context.createIntent(function: Intent.() -> Unit = {}) = Intent(this, T::class.java).apply(function)

inline fun <reified T : Activity> Activity.startNewActivityFinishCurrent() {
    startActivity<T>()
    finish()
}

inline fun <reified T : Activity> Context.startActivity(function: Intent.() -> Unit = {}) = startActivity(createIntent<T>(function))

fun Activity.startActivityForResult(intent: Intent, requestCode: Int = 0) = startActivityForResult(intent, requestCode)

fun Activity.startWebsiteFromUrl(url: String) = startActivity(Intent(Intent.ACTION_VIEW).apply { data = Uri.parse(url) })
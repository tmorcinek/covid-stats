package com.morcinek.covid.core.extensions

import com.github.mikephil.charting.formatter.ValueFormatter

fun valueFormatter(formattedValue: (value: Float) -> String) = object : ValueFormatter() {
    override fun getFormattedValue(value: Float): String = formattedValue(value)
}

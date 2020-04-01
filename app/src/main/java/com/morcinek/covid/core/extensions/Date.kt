package com.morcinek.covid.core.extensions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date.toDayMonthDateFormat() = dayMonthDateFormat().format(this)
fun Date.toStandardString() = standardDateFormat().format(this)

fun standardDateFormat() = SimpleDateFormat("dd.MM.yyyy", Locale.US)
fun dayMonthDateFormat() = SimpleDateFormat("dd.MM", Locale.US)

fun DateFormat.formatCalendar(date: Date): String = format(date.time)
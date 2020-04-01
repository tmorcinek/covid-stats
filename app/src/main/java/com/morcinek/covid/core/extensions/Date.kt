package com.morcinek.covid.core.extensions

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun Date.toDayOfWeekDateFormat() = dayOfWeekDateFormat().format(this)
fun Date.toStandardString() = standardDateFormat().format(this)


fun standardDateFormat() = SimpleDateFormat("dd.MM.yyyy", Locale.US)
fun dayOfWeekDateFormat() = SimpleDateFormat("EEEE dd.MM.yyyy", Locale.US)

fun DateFormat.formatCalendar(date: Date): String = format(date.time)
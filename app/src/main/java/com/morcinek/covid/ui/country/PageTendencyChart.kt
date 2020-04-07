package com.morcinek.covid.ui.country

import androidx.fragment.app.Fragment
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.morcinek.covid.R
import com.morcinek.covid.core.extensions.Page
import com.morcinek.covid.core.extensions.dayMonthDateFormat
import com.morcinek.covid.core.extensions.observe
import com.morcinek.covid.core.extensions.valueFormatter
import kotlinx.android.synthetic.main.view_line_chart.view.*
import java.util.*
import java.util.concurrent.TimeUnit


internal fun CountryFragment.tendencyChartPage() = Page(R.string.page_chart, R.layout.view_line_chart) {
    chart.apply {
        setTouchEnabled(true)
        setDrawGridBackground(true)
        description.isEnabled = false
        axisLeft.isEnabled = false
        xAxis.valueFormatter = dateValueFormatter()

        observe(viewModel.countryData) { countryData ->
            data = LineData(
                listOf(
                    lineDataSet(countryData.first, R.string.page_deaths, colorRed),
                    lineDataSet(countryData.second, R.string.page_confirmed, colorBlue),
                    lineDataSet(countryData.third, R.string.page_recovered, colorGreen)
                )
            )
            invalidate()
        }
    }
}

internal fun Date.toDays() = TimeUnit.MILLISECONDS.toDays(time).toFloat()
internal fun Float.toMillis() = Date(TimeUnit.DAYS.toMillis(toLong()))


private val dateFormat = dayMonthDateFormat()

internal fun dateValueFormatter() = valueFormatter { dateFormat.format(it.toMillis()) }


private fun toEntry(): (DayData) -> Entry = { Entry(it.Date.toDays(), it.Cases.toFloat()) }

private fun Fragment.lineDataSet(data: List<DayData>, titleRes: Int, color: Int) = LineDataSet(data.map(toEntry()), getString(titleRes)).apply {
    setCircleColor(color)
    setColor(color)
    lineWidth = 2f
    setDrawCircles(false)
}


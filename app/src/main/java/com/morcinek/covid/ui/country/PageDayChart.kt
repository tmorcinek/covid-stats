package com.morcinek.covid.ui.country

import androidx.lifecycle.LiveData
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.morcinek.covid.R
import com.morcinek.covid.core.extensions.Page
import com.morcinek.covid.core.extensions.observe
import kotlinx.android.synthetic.main.view_bar_chart.view.*

internal fun CountryFragment.confirmedPage() = byDayPage(R.string.page_confirmed, R.layout.view_bar_chart, colorBlue, viewModel.confirmedDayData())
internal fun CountryFragment.deathPage() = byDayPage(R.string.page_deaths, R.layout.view_bar_chart, colorRed, viewModel.deathsDayData())
internal fun CountryFragment.recoveredPage() = byDayPage(R.string.page_recovered, R.layout.view_bar_chart, colorGreen, viewModel.recoveredDayData())

private fun CountryFragment.byDayPage(title: Int, layoutId: Int, color: Int, liveData: LiveData<List<DayData>>) = Page(title, layoutId) {
    barChart.apply {
        setDrawValueAboveBar(true)
        setTouchEnabled(true)
        description.isEnabled = false
        legend.isEnabled = false
        axisLeft.isEnabled = false
        xAxis.apply {
            valueFormatter = dateValueFormatter()
            granularity = 1f
        }

        observe(liveData) { dayData ->
            data = BarData(BarDataSet(dayData.map(toBarEntry()), "").apply {
                setColors(color)
                valueFormatter = DefaultValueFormatter(0)
            }).apply { barWidth = 0.9f }
            invalidate()
        }
    }
}

private fun toBarEntry(): (DayData) -> BarEntry = { BarEntry(it.Date.toDays(), it.Cases.toFloat()) }

package com.morcinek.covid.ui.country

import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import com.morcinek.covid.R
import com.morcinek.covid.core.extensions.Page
import kotlinx.android.synthetic.main.view_country_summary.view.*
import kotlinx.android.synthetic.main.view_value_horizontal.view.*

internal fun CountryFragment.summaryPage() = Page(R.string.page_summary, R.layout.view_country_summary) {
    viewModel.summaryCountry.let {
        pieChart.apply {
            isRotationEnabled = false
            description.isEnabled = false
            setUsePercentValues(true)
            setDrawEntryLabels(false)
            data = PieData(PieDataSet(
                listOf(
                    PieEntry(it.totalActive().toFloat().div(it.TotalConfirmed), getString(R.string.active)),
                    PieEntry(it.TotalDeaths.toFloat().div(it.TotalConfirmed), getString(R.string.page_deaths)),
                    PieEntry(it.TotalRecovered.toFloat().div(it.TotalConfirmed), getString(R.string.page_recovered))
                ), ""
            ).apply {
                sliceSpace = 5f
                colors = listOf(colorBlue, colorRed, colorGreen)
            }).apply {
                setValueFormatter(PercentFormatter(pieChart))
                setValueTextColor(colorText)
                setValueTextSize(16f)
            }
        }
        rateLayout.apply {
            title.setText(R.string.rate_title)
            value.text = getString(R.string.percentage_format, it.deathRate())
        }
        deathRecoveredRatio.apply {
            title.setText(R.string.death_recovered_ratio_title)
            value.text = getString(R.string.ratio_format, it.deathRecoverRatio())
        }
        confirmedLayout.apply {
            title.setText(R.string.confirmed_title)
            subtitle.setText(R.string.confirmed_subtitle)
            value.text = "${it.TotalConfirmed}"
        }
        deathsLayout.apply {
            title.setText(R.string.deaths_title)
            subtitle.setText(R.string.deaths_subtitle)
            value.text = "${it.TotalDeaths}"
        }
        recoveredLayout.apply {
            title.setText(R.string.recovered_title)
            subtitle.setText(R.string.recovered_subtitle)
            value.text = "${it.TotalRecovered}"
        }
    }
}

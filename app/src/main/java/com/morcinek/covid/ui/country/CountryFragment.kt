package com.morcinek.covid.ui.country

import android.os.Bundle
import android.text.format.DateUtils
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.PercentFormatter
import com.morcinek.covid.R
import com.morcinek.covid.core.BaseFragment
import com.morcinek.covid.core.extensions.*
import com.morcinek.covid.getApi
import com.morcinek.covid.ui.countries.SummaryCountry
import kotlinx.android.synthetic.main.fragment_pager.view.*
import kotlinx.android.synthetic.main.view_country_summary.view.*
import kotlinx.android.synthetic.main.view_line_chart.view.*
import kotlinx.android.synthetic.main.view_value_horizontal.view.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*


class CountryFragment : BaseFragment(R.layout.fragment_pager) {

    private val viewModel by viewModelWithFragment<CountryViewModel>()

    private val dateFormat = dayMonthDateFormat()

    private val colorText by lazy { getColor(requireContext(), R.color.text) }
    private val colorBlue by lazy { getColor(requireContext(), R.color.lightBlue) }
    private val colorGreen by lazy { getColor(requireContext(), R.color.green) }
    private val colorRed by lazy { getColor(requireContext(), R.color.red) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            tabLayout.setupWithViewPager(viewPager)
            viewPager.adapter = pageAdapter(summaryPage(), chartPage())
        }
    }

    private fun summaryPage() = Page(R.string.page_summary, R.layout.view_country_summary) {
        viewModel.summaryCountry.let {
            pieChart.apply {
                isRotationEnabled = false
                description.isEnabled = false
                setUsePercentValues(true)
                setEntryLabelColor(colorText)
                setEntryLabelTextSize(12f)
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
                value.text = "${it.deathRate()}%"
            }
            deathRecoveredRatio.apply {
                title.setText(R.string.death_recovered_ratio_title)
                value.text = "${it.TotalDeaths.toFloat().div(it.TotalRecovered)}"
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

    private fun chartPage() = Page(R.string.page_chart, R.layout.view_line_chart) {
        chart.apply {
            description.isEnabled = false
            axisLeft.isEnabled = false
            setTouchEnabled(true)
            setDrawGridBackground(true)
            xAxis.apply {
                valueFormatter = valueFormatter { dateFormat.format(Date(it.toLong())) }
                granularity = DateUtils.DAY_IN_MILLIS.toFloat()
            }
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

    private fun lineDataSet(data: List<DayData>, titleRes: Int, color: Int) = LineDataSet(data.toEntries(), getString(titleRes)).apply {
        setCircleColor(color)
        setColor(color)
        lineWidth = 2f
        setDrawCircles(false)
    }

    private fun List<DayData>.toEntries() = map { Entry(it.Date.time.toFloat(), it.Cases.toFloat()) }
}

val countryModule = module {
    viewModel { (fragment: Fragment) -> CountryViewModel(getApi(), fragment.getParcelable()) }
}

private class CountryViewModel(val api: CountryApi, val summaryCountry: SummaryCountry) : ViewModel() {

    private fun data(status: String) = liveData(Dispatchers.IO) { emit(api.getData(summaryCountry.Slug, status)) }

    val countryData = combine(data("deaths"), data("confirmed"), data("recovered"))
}


package com.morcinek.covid.ui.country

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.mikephil.charting.data.Entry
import com.morcinek.covid.R
import com.morcinek.covid.core.BaseFragment
import com.morcinek.covid.core.extensions.*
import com.morcinek.covid.getApi
import com.morcinek.covid.ui.summary.SummaryCountry
import kotlinx.android.synthetic.main.fragment_pager.view.*
import kotlinx.android.synthetic.main.view_country_summary.view.*
import kotlinx.android.synthetic.main.view_value_horizontal.view.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


class CountryFragment : BaseFragment(R.layout.fragment_pager) {

    private val viewModel by viewModelWithFragment<CountryViewModel>()

    private val dateFormat = dayMonthDateFormat()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            tabLayout.setupWithViewPager(viewPager)
            tabLayout.isVisible = false
//            viewPager.adapter = singlePageAdapter(R.layout.view_line_chart) {
//                chart.apply {
//                    description.isEnabled = false
//                    axisLeft.isEnabled = false
//                    setTouchEnabled(true)
//                    setDrawGridBackground(true)
//                    xAxis.apply {
//                        valueFormatter = valueFormatter { dateFormat.format(Date(it.toLong())) }
//                        granularity = DateUtils.DAY_IN_MILLIS.toFloat()
//                    }
//
//                    observe(viewModel.countryData) { countryData ->
//                        data = LineData(
//                            listOf(
//                                LineDataSet(countryData.first.toEntries(), "Deaths").apply {
//                                    setColor(Color.RED)
//                                    setCircleColor(Color.RED)
//                                    setDrawCircles(false)
//                                    setLineWidth(2f)
//                                },
//                                LineDataSet(countryData.second.toEntries(), "Confirmed").apply {
//                                    setDrawCircles(false)
//                                    setLineWidth(2f)
//                                },
//                                LineDataSet(countryData.third.toEntries(), "Recovered").apply {
//                                    setCircleColor(Color.rgb(40, 255, 40))
//                                    setColor(Color.GREEN)
//                                    setDrawCircles(false)
//                                }
//                            )
//                        )
//                        invalidate()
//                    }
//                }
//            }.apply { notifyDataSetChanged() }
            viewPager.adapter = singlePageAdapter(R.layout.view_country_summary) {
                viewModel.summaryCountry.let {
                    rateLayout.apply {
                        title.setText(R.string.rate_title)
                        subtitle.setText(R.string.rate_subtitle)
                        val rate = it.TotalDeaths * 100f / it.TotalConfirmed.toFloat()
                        value.text = "$rate%"
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
        }
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


package com.morcinek.covid.ui.country

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.morcinek.covid.R
import com.morcinek.covid.core.BaseFragment
import com.morcinek.covid.core.extensions.*
import com.morcinek.covid.getApi
import com.morcinek.covid.ui.summary.SummaryCountry
import kotlinx.android.synthetic.main.fragment_pager.view.*
import kotlinx.android.synthetic.main.view_line_chart.view.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.util.*


class CountryFragment : BaseFragment(R.layout.fragment_pager) {

    private val viewModel by viewModelWithFragment<CountryViewModel>()

    private val dateFormat = dayMonthDateFormat()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            tabLayout.setupWithViewPager(viewPager)
            tabLayout.isVisible = false
//            viewPager.adapter = recyclerViewPagerAdapter(
//                R.string.page_confirmed to dayAdapter("confirmed"),
//                R.string.page_deaths to dayAdapter("deaths"),
//                R.string.page_recovered to dayAdapter("recovered")
//            )
            viewPager.adapter = singlePageAdapter(R.layout.view_line_chart) {
                chart.apply {
                    description.isEnabled = false
                    axisLeft.isEnabled = false
                    setTouchEnabled(true)
                    setDrawGridBackground(true)
//                    observe(viewModel.data("deaths")) {
//                        val values = it.map { Entry(it.Date.time.toFloat(), it.Cases.toFloat()) }
//                        data = LineData(listOf(LineDataSet(values, "Deaths")))
//                        invalidate()
//                    }
                    observe(viewModel.countryData) { countryData ->
                        data = LineData(
                            listOf(
                                LineDataSet(entries(countryData.first), "Deaths").apply {
                                    setColor(Color.RED)
                                    setCircleColor(Color.RED)
                                    setDrawCircles(false)
                                    setLineWidth(2f)
                                },
                                LineDataSet(entries(countryData.second), "Confirmed").apply {
                                    setDrawCircles(false)
                                    setLineWidth(2f)
                                },
                                LineDataSet(entries(countryData.third), "Recovered").apply {
                                    setCircleColor(Color.rgb(40, 255, 40))
                                    setColor(Color.GREEN)
                                    setDrawCircles(false)
                                }
                            )
                        )
                        invalidate()
                    }
                    xAxis.apply {
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float) = dateFormat.format(Date(value.toLong()))
                        }
                        granularity = 1000f * 60 * 60 * 24
                    }
                }
//                addPlayerButton.setOnClickListener { navController.navigate(R.id.nav_create_player, viewModel.teamData.toBundle()) }
//                deleteButton.setOnClickListener {
//                    showDeleteCodeConfirmationDialog(
//                        R.string.team_delete_query,
//                        R.string.team_delete_message
//                    ) { viewModel.deleteTeam { navController.popBackStack() } }
//                }
            }.apply { notifyDataSetChanged() }

        }
    }

    private fun entries(it: List<DayData>) = it.map { Entry(it.Date.time.toFloat(), it.Cases.toFloat()) }

//    private fun dayAdapter(status: String) =
//        listAdapter(R.layout.vh_day, itemCallback { areItemsTheSame { t1, t2 -> t1.Date == t2.Date } }) { _, item: DayData ->
//            title.text = dateFormat.format(item.Date)
//            subtitle.text = "${item.Cases}"
//        }.apply { observe(viewModel.data(status)) { submitList(it) } }
}

val countryModule = module {
    viewModel { (fragment: Fragment) -> CountryViewModel(getApi(), fragment.getParcelable()) }
}

private class CountryViewModel(val api: CountryApi, val summaryCountry: SummaryCountry) : ViewModel() {

    private fun data(status: String) = liveData(Dispatchers.IO) { emit(api.getData(summaryCountry.Slug, status)) }

    val countryData = combine(data("deaths"), data("confirmed"), data("recovered"))
}


package com.morcinek.covid.ui.country

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.morcinek.covid.R
import com.morcinek.covid.core.BaseFragment
import com.morcinek.covid.core.extensions.*
import com.morcinek.covid.getApi
import com.morcinek.covid.ui.countries.SummaryCountry
import kotlinx.android.synthetic.main.fragment_pager.view.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


class CountryFragment : BaseFragment(R.layout.fragment_pager) {

    internal val viewModel by viewModelWithFragment<CountryViewModel>()

    internal val colorText by lazy { getColor(requireContext(), R.color.textSecondary) }
    internal val colorBlue by lazy { getColor(requireContext(), R.color.lightBlue) }
    internal val colorGreen by lazy { getColor(requireContext(), R.color.green) }
    internal val colorRed by lazy { getColor(requireContext(), R.color.red) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            tabLayout.setupWithViewPager(viewPager)
            viewPager.adapter = pageAdapter(summaryPage(), tendencyChartPage(), confirmedPage(), deathPage(), recoveredPage())
        }
    }
}

val countryModule = module {
    viewModel { (fragment: Fragment) -> CountryViewModel(getApi(), fragment.getParcelable()) }
}

internal class CountryViewModel(private val api: CountryApi, val summaryCountry: SummaryCountry) : ViewModel() {

    private fun data(status: String) = liveData(Dispatchers.IO) { emit(api.getData(summaryCountry.Slug, status)) }

    private val deathsData = data("deaths")
    private val confirmedData = data("confirmed")
    private val recoveredData = data("recovered")

    val countryData = combine(deathsData, confirmedData, recoveredData)

    fun deathsDayData() = deathsData.map(previousDifferenceMapping())
    fun confirmedDayData() = confirmedData.map(previousDifferenceMapping())
    fun recoveredDayData() = recoveredData.map(previousDifferenceMapping())

    private fun previousDifferenceMapping(): (List<DayData>) -> List<DayData> = {
        it.mapIndexed { index, dayData ->
            try {
                DayData(dayData.Date, dayData.Cases - it[index - 1].Cases)
            } catch (e: IndexOutOfBoundsException) {
                dayData
            }
        }
    }
}


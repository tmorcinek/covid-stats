package com.morcinek.covid.ui.country

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.morcinek.covid.R
import com.morcinek.covid.core.BaseFragment
import com.morcinek.covid.core.extensions.*
import com.morcinek.covid.core.itemCallback
import com.morcinek.covid.core.listAdapter
import com.morcinek.covid.getApi
import com.morcinek.covid.ui.summary.SummaryCountry
import kotlinx.android.synthetic.main.fragment_pager.view.*
import kotlinx.android.synthetic.main.vh_day.view.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


class CountryFragment : BaseFragment(R.layout.fragment_pager) {

    private val viewModel by viewModelWithFragment<CountryViewModel>()

    private val dateFormat = standardDateFormat()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply {
            tabLayout.setupWithViewPager(viewPager)
            viewPager.adapter = recyclerViewPagerAdapter(
                R.string.page_confirmed to dayAdapter("confirmed"),
                R.string.page_deaths to dayAdapter("deaths"),
                R.string.page_recovered to dayAdapter("recovered")
            )
        }
    }

    private fun dayAdapter(status: String) =
        listAdapter(R.layout.vh_day, itemCallback { areItemsTheSame { t1, t2 -> t1.Date == t2.Date } }) { _, item: DayData ->
            title.text = dateFormat.format(item.Date)
            subtitle.text = "${item.Cases}"
        }.apply { observe(viewModel.data(status)) { submitList(it) } }
}

val countryModule = module {
    viewModel { (fragment: Fragment) -> CountryViewModel(getApi(), fragment.getParcelable()) }
}

private class CountryViewModel(val api: CountryApi, val summaryCountry: SummaryCountry) : ViewModel() {

    fun data(status: String) = liveData(Dispatchers.IO) { emit(api.getData(summaryCountry.Slug, status)) }
}


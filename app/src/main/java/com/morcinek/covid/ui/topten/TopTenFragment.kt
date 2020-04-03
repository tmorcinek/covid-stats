package com.morcinek.covid.ui.topten

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.covid.R
import com.morcinek.covid.core.BaseFragment
import com.morcinek.covid.core.createMenuConfiguration
import com.morcinek.covid.core.extensions.alert.selector
import com.morcinek.covid.core.extensions.combine
import com.morcinek.covid.core.extensions.mutableValueLiveData
import com.morcinek.covid.core.extensions.observe
import com.morcinek.covid.core.extensions.toBundleWithTitle
import com.morcinek.covid.core.itemCallback
import com.morcinek.covid.core.listAdapter
import com.morcinek.covid.getApi
import com.morcinek.covid.ui.countries.SummaryApi
import com.morcinek.covid.ui.countries.SummaryCountry
import com.morcinek.covid.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_summary.view.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module


class TopTenFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModel<TopTenViewModel>()

    private val navController: NavController by lazyNavController()

    override val menuConfiguration = createMenuConfiguration {
        addAction(R.string.sort_by, R.drawable.ic_sort) {
            selector(R.string.sort_by, sortingMethods.map { getString(it.text) }) { _, index -> viewModel.selectSortingMethod(sortingMethods[index]) }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        view.progressBar.show()
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter =
                listAdapter(R.layout.vh_summary, itemCallback { areItemsTheSame { t1, t2 -> t1.Country == t2.Country } }) { _, item: SummaryCountry ->
                    title.text = item.Country
                    newConfirmed.text = "${item.NewConfirmed}"
                    newDeaths.text = "${item.NewDeaths}"
                    totalConfirmed.text = "${item.TotalConfirmed}"
                    totalDeaths.text = "${item.TotalDeaths}"
                    newRecovered.text = "${item.NewRecovered}"
                    totalRecovered.text = "${item.TotalRecovered}"
                    setOnClickListener { navController.navigate(R.id.nav_days, item.toBundleWithTitle { Country }) }
                }.apply {
                    observe(viewModel.countriesData) {
                        submitList(it)
                        view.progressBar.hide()
                    }
                }
        }
    }
}

val topTenModule = module {
    viewModel { TopTenViewModel(getApi()) }
}

private class TopTenViewModel(val summaryApi: SummaryApi) : ViewModel() {

    private val data = liveData(Dispatchers.IO) { emit(summaryApi.getData()) }

    private val selectedSortingMethod = mutableValueLiveData(sortingMethods.first())

    val countriesData = combine(data, selectedSortingMethod) { summaryData, sortingMethod ->
        summaryData.Countries.distinctBy { it.Slug }.sortedByDescending(sortingMethod.function).take(10)
    }

    fun selectSortingMethod(sortingMethod: SortingMethod) = selectedSortingMethod.postValue(sortingMethod)
}

private class SortingMethod(val text: Int, val function: (SummaryCountry) -> Int)

private val sortingMethods = listOf(
    SortingMethod(R.string.sorting_total_confirmed) { it.TotalConfirmed },
    SortingMethod(R.string.sorting_total_deaths) { it.TotalDeaths },
    SortingMethod(R.string.sorting_total_recovered) { it.TotalRecovered }
)

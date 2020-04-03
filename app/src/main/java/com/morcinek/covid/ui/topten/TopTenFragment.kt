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
import kotlinx.android.synthetic.main.vh_title_value.view.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module


class TopTenFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModel<TopTenViewModel>()

    private val navController: NavController by lazyNavController()

    //    override val fabConfiguration = FabConfiguration({ navController.navigate(R.id.nav_how_many_players) })

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
                listAdapter(R.layout.vh_title_value, itemCallback {
                    areItemsTheSame { t1, t2 -> t1.title == t2.title }
                    areContentsTheSame { t1, t2 -> t1.value == t2.value }
                }) { _, item: TitleValue ->
                    title.text = item.title
                    value.text = item.value
                    setOnClickListener { navController.navigate(R.id.nav_days, item.summaryCountry.toBundleWithTitle { Country }) }
                }.apply {
                    observe(viewModel.countriesData) {
                        submitList(it) { scrollToPosition(0) }
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
        summaryData.Countries.distinctBy { it.Slug }.filter { it.hasData() }.run(sortingMethod.sorting).reversed()
            .take(10).map { TitleValue(it.Country, sortingMethod.formatting(it), it) }
    }

    fun selectSortingMethod(sortingMethod: SortingMethod) = selectedSortingMethod.postValue(sortingMethod)
}

private data class TitleValue(val title: String, val value: String, val summaryCountry: SummaryCountry)

private class SortingMethod(val text: Int, val sorting: Iterable<SummaryCountry>.() -> Iterable<SummaryCountry>, val formatting: SummaryCountry.() -> String)

private val sortingMethods = listOf(
    SortingMethod(R.string.sorting_total_confirmed, { sortedBy { it.TotalConfirmed } }, { TotalConfirmed.toString() }),
    SortingMethod(R.string.sorting_total_deaths, { sortedBy { it.TotalDeaths } }, { TotalDeaths.toString() }),
    SortingMethod(R.string.sorting_total_recovered, { sortedBy { it.TotalRecovered } }, { TotalRecovered.toString() }),
    SortingMethod(R.string.sorting_rate, { sortedBy { it.deathRate() } }, { "${deathRate()}%" })
)

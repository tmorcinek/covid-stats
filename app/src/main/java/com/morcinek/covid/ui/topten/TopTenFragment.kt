package com.morcinek.covid.ui.topten

import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.covid.R
import com.morcinek.covid.core.*
import com.morcinek.covid.core.extensions.alert.singleChoiceSelector
import com.morcinek.covid.core.extensions.combine
import com.morcinek.covid.core.extensions.mutableValueLiveData
import com.morcinek.covid.core.extensions.observe
import com.morcinek.covid.core.extensions.toBundleWithTitle
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
        addAction(R.string.sort_by, R.drawable.ic_filter) {
            singleChoiceSelector(R.string.sort_by, getString(viewModel.sortingMethod.text), sortingMethods.map { getString(it.text) }) { _, index ->
                viewModel.updateFilterData {
                    sortingMethod = sortingMethods[index]
                }
            }
        }
        addAction(R.string.isDescending, R.drawable.ic_arrow_upward) {
            viewModel.updateFilterData { isDescending = !isDescending }
            invalidateOptionsMenu()
        }
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        menu.findItem(R.string.isDescending).setIcon(if (viewModel.isDescending) R.drawable.ic_arrow_upward else R.drawable.ic_arrow_downward)
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
                    setOnClickListener { navController.navigate(R.id.nav_country, item.summaryCountry.toBundleWithTitle { Country }) }
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

    private val filterData = mutableValueLiveData(FilterData(true, sortingMethods.first()))

    val sortingMethod : SortingMethod
        get() = filterData.value!!.sortingMethod

    val isDescending: Boolean
        get() = filterData.value!!.isDescending

    val countriesData = combine(data, filterData) { summaryData, filterData ->
        summaryData.Countries.distinctBy { it.Slug }.filter { it.hasData() }.run(filterData.sortingMethod.sorting)
            .run { if (filterData.isDescending) reversed() else this }
            .map { TitleValue(it.Country, filterData.sortingMethod.formatting(it), it) }
    }

    fun updateFilterData(update: FilterData.() -> Unit) = filterData.postValue(filterData.value!!.apply(update))
}

private data class TitleValue(val title: String, val value: String, val summaryCountry: SummaryCountry)

private class FilterData(var isDescending: Boolean, var sortingMethod: SortingMethod)

private class SortingMethod(val text: Int, val sorting: Iterable<SummaryCountry>.() -> Iterable<SummaryCountry>, val formatting: SummaryCountry.() -> String)

private val sortingMethods = listOf(
    SortingMethod(R.string.sorting_total_confirmed, { sortedBy { it.TotalConfirmed } }, { TotalConfirmed.toString() }),
    SortingMethod(R.string.sorting_total_deaths, { sortedBy { it.TotalDeaths } }, { TotalDeaths.toString() }),
    SortingMethod(R.string.sorting_total_recovered, { sortedBy { it.TotalRecovered } }, { TotalRecovered.toString() }),
    SortingMethod(R.string.sorting_rate, { sortedBy { it.deathRate() } }, { "${deathRate()}%" })
)


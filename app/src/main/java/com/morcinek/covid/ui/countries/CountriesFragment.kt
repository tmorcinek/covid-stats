package com.morcinek.covid.ui.countries

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.covid.R
import com.morcinek.covid.core.BaseFragment
import com.morcinek.covid.core.extensions.*
import com.morcinek.covid.core.itemCallback
import com.morcinek.covid.core.listAdapter
import com.morcinek.covid.getApi
import com.morcinek.covid.ui.lazyNavController
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_summary.view.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module


class SummaryFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModel<SummaryViewModel>()

    private val navController: NavController by lazyNavController()

//    override val fabConfiguration = FabConfiguration({ navController.navigate(R.id.nav_how_many_players) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        view.progressBar.show()
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = listAdapter(R.layout.vh_summary, itemCallback { areItemsTheSame { t1, t2 -> t1.Country == t2.Country } }) { position, item: SummaryCountry ->
                subtitle.text = "${position + 1}"
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main, menu)
        menu.findItem(R.id.action_search).apply {
            (actionView as SearchView).setOnQueryTextChange { viewModel.updateSearchText(it) }
            setOnMenuItemActionCollapse { viewModel.updateSearchText("") }
        }
    }
}

val summaryModule = module {
    viewModel { SummaryViewModel(getApi()) }
}

private class SummaryViewModel(val summaryApi: SummaryApi) : ViewModel() {

    private val data = liveData(Dispatchers.IO) { emit(summaryApi.getData()) }
    private val searchTextData = mutableValueLiveData("")

    val countriesData = combine(data, searchTextData) { summaryData, text ->
        summaryData.Countries.filter { it.Country.contains(text, true) }.distinctBy { it.Slug }.sortedByDescending { it.TotalConfirmed }
    }

    fun updateSearchText(text: String) = searchTextData.postValue(text)
}


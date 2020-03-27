package com.morcinek.covid.ui.summary

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.covid.R
import com.morcinek.covid.core.BaseFragment
import com.morcinek.covid.core.extensions.observe
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
        view.progressBar.show()
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = listAdapter(R.layout.vh_summary, itemCallback { areItemsTheSame { t1, t2 -> t1.Country == t2.Country } }) { _, item: SummaryCountry ->
                title.text = item.Country
                subtitle.text = "New Deaths/Confirmed ${item.NewDeaths}/ ${item.NewConfirmed}"
                path.text = "Total Deaths/Confirmed ${item.TotalDeaths}/ ${item.TotalConfirmed}"
                params.text = "Recoverd ${item.NewRecovered}/ ${item.TotalRecovered}"
//                setOnClickListener { navController.navigate(R.id.nav_tournament_details, item.toBundle()) }
            }.apply {
                observe(viewModel.data) {
                    submitList(it.Countries.sortedByDescending { it.TotalConfirmed })
                    view.progressBar.hide()
                }
            }
        }
    }
}

val summaryModule = module {
    viewModel { SummaryViewModel(getApi()) }
}

private class SummaryViewModel(val summaryApi: SummaryApi) : ViewModel() {

    val data = liveData(Dispatchers.IO) {
        emit(summaryApi.getData())
    }
}


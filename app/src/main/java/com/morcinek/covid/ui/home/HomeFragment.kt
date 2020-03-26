package com.morcinek.covid.ui.home

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.morcinek.covid.R
import com.morcinek.covid.core.BaseFragment
import com.morcinek.covid.ui.lazyNavController
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class HomeFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModel<TournamentsViewModel>()

    private val navController: NavController by lazyNavController()

//    override val fabConfiguration = FabConfiguration({ navController.navigate(R.id.nav_how_many_players) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        view.recyclerView.apply {
//            layoutManager = LinearLayoutManager(activity)
//            adapter = listAdapter(R.layout.vh_tournament, itemCallback { areItemsTheSame { t1, t2 -> t1.id == t2.id } }) { _, item: TournamentData ->
//                title.text = item.title
//                subtitle.text = item.subtitle
//                finished.text = item.finished
//                isToday.isVisible = item.isToday
//                setOnClickListener { navController.navigate(R.id.nav_tournament_details, item.toBundle()) }
//            }.apply {
//                observe(viewModel.tournaments) { submitList(it) }
//            }
//        }
    }
}

val funinoModule = module {
    viewModel { TournamentsViewModel() }
}

private class TournamentsViewModel : ViewModel() {

//    val tournaments: LiveData<List<TournamentData>> = MutableLiveData<List<TournamentData>>()
}


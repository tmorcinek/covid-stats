package com.morcinek.covid.ui.home

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
import kotlinx.android.synthetic.main.vh_home.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.dsl.module

class HomeFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModel<HomeViewModel>()

    private val navController: NavController by lazyNavController()

//    override val fabConfiguration = FabConfiguration({ navController.navigate(R.id.nav_how_many_players) })

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = listAdapter(R.layout.vh_home, itemCallback { areItemsTheSame { t1, t2 -> t1.Name == t2.Name } }) { _, item: HomeData ->
                title.text = item.Name
                subtitle.text = item.Description
                path.text = item.Path
                params.text = item.Params?.joinToString { "," } ?: ""
//                setOnClickListener { navController.navigate(R.id.nav_tournament_details, item.toBundle()) }
            }.apply {
                observe(viewModel.data) { submitList(it) }
            }
        }
    }
}

val homeModule = module {
    viewModel { HomeViewModel(getApi()) }
}

private class HomeViewModel(val homeApi: HomeApi) : ViewModel() {

    val data = liveData(Dispatchers.IO){
        emit(homeApi.getData())
    }
}


package com.morcinek.covid.ui.days

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.recyclerview.widget.LinearLayoutManager
import com.morcinek.covid.R
import com.morcinek.covid.core.BaseFragment
import com.morcinek.covid.core.extensions.getParcelable
import com.morcinek.covid.core.extensions.observe
import com.morcinek.covid.core.extensions.standardDateFormat
import com.morcinek.covid.core.extensions.viewModelWithFragment
import com.morcinek.covid.core.itemCallback
import com.morcinek.covid.core.listAdapter
import com.morcinek.covid.getApi
import com.morcinek.covid.ui.summary.DaysApi
import com.morcinek.covid.ui.summary.DaysData
import com.morcinek.covid.ui.summary.SummaryCountry
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.vh_day.view.*
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


class DaysFragment : BaseFragment(R.layout.fragment_list) {

    private val viewModel by viewModelWithFragment<DaysViewModel>()

    private val dateFormat = standardDateFormat()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        view.progressBar.show()
        view.recyclerView.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = listAdapter(R.layout.vh_day, itemCallback { areItemsTheSame { t1, t2 -> t1.Date == t2.Date } }) { _, item: DaysData ->
                title.text = dateFormat.format(item.Date)
                subtitle.text = "${item.Cases}"
            }.apply {
                observe(viewModel.data) {
                    submitList(it)
                    view.progressBar.hide()
                }
            }
        }
    }
}

val daysModule = module {
    viewModel { (fragment: Fragment) -> DaysViewModel(getApi(), fragment.getParcelable()) }
}

private class DaysViewModel(val api: DaysApi, val summaryCountry: SummaryCountry) : ViewModel() {

    val data = liveData(Dispatchers.IO) { emit(api.getData(summaryCountry.Slug, "confirmed")) }
}


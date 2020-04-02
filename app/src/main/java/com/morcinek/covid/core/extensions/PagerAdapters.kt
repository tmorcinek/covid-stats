package com.morcinek.covid.core.extensions

import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter


fun Fragment.recyclerViewPagerAdapter(vararg pagers: Pair<Int, RecyclerView.Adapter<out RecyclerView.ViewHolder>>): PagerAdapter = object : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun getCount() = pagers.size

    override fun getPageTitle(position: Int) = requireContext().getString(pagers[position].first)

    override fun instantiateItem(container: ViewGroup, position: Int) = RecyclerView(requireContext()).apply {
        layoutManager = LinearLayoutManager(requireContext())
        adapter = pagers[position].second
    }.also { container.addView(it) }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)
}

fun Fragment.singlePageAdapter(layoutId: Int, initialization: View.() -> Unit): PagerAdapter = object : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun getCount() = 1

    override fun instantiateItem(container: ViewGroup, position: Int) =
        layoutInflater.inflate(layoutId, container, false).apply(initialization).also { container.addView(it) }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)
}

fun Fragment.pageAdapter(vararg pagers: Page): PagerAdapter = object : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any) = view == `object`

    override fun getCount() = pagers.size

    override fun getPageTitle(position: Int) = requireContext().getString(pagers[position].title)

    override fun instantiateItem(container: ViewGroup, position: Int) = pagers[position].let { page ->
        layoutInflater.inflate(page.layoutId, container, false).apply(page.initialization).also { container.addView(it) }
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) = container.removeView(`object` as View)
}

class Page (
    val title: Int,
    val layoutId: Int,
    val initialization: View.() -> Unit
)
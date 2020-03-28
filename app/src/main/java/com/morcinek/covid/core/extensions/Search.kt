package com.morcinek.covid.core.extensions

import android.view.MenuItem
import androidx.appcompat.widget.SearchView

fun MenuItem.setOnMenuItemActionCollapse(onMenuItemActionCollapseListener: (menuItem: MenuItem?) -> Unit): MenuItem =
    setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
        override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
            return true
        }

        override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
            onMenuItemActionCollapseListener(item)
            return true
        }
    })

fun SearchView.setOnQueryTextChange(onQueryTextChange: (text: String) -> Unit) {
    setOnQueryTextListener(object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String): Boolean { // Toast like print
            return false
        }

        override fun onQueryTextChange(text: String): Boolean {
            onQueryTextChange(text)
            return false
        }
    })
}

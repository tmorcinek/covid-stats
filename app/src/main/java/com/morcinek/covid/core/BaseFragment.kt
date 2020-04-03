package com.morcinek.covid.core

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.morcinek.covid.R
import com.morcinek.covid.core.extensions.hide
import com.morcinek.covid.core.extensions.safeLet
import kotlinx.android.synthetic.main.app_bar_main.*

abstract class BaseFragment(private val layoutResourceId: Int) : Fragment() {

    open val menuConfiguration: MenuConfiguration? = null
    open val fabConfiguration: FabConfiguration? = null

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(layoutResourceId, container, false)!!

    override fun onResume() {
        super.onResume()

        requireActivity().fab.let { fab ->
            if (fab.isOrWillBeShown) fab.hide {
                initializeFab(fab)
            } else {
                initializeFab(fab)
            }
        }
    }

    private fun initializeFab(fab: FloatingActionButton) {
        fabConfiguration?.let {
            fab.apply {
                setImageResource(it.fabIcon)
                setOnClickListener(it.fabActon)
                show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        menuConfiguration?.let { setHasOptionsMenu(true) }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        menuConfiguration?.let {
            it.actions.find { it.textRes == item.itemId }?.let { menuConfigurationItem ->
                menuConfigurationItem.action.invoke()
                return true
            }
        } ?: return false
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) = menuConfiguration.safeLet {
        it.actions.forEachIndexed { index, item ->
            menu.add(Menu.NONE, item.textRes, index, item.textRes)
                .setIcon(item.iconRes)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
        }
    }
}

class FabConfiguration(val fabActon: (View) -> Unit, val fabIcon: Int = R.drawable.ic_add)

fun createFabConfiguration(fabIcon: Int = R.drawable.ic_add, fabActon: (View) -> Unit) = FabConfiguration(fabActon, fabIcon)

class MenuConfiguration {
    internal val actions = mutableListOf<MenuConfigurationItem>()

    fun addAction(textRes: Int, iconRes: Int, action: () -> Any) {
        actions.add(MenuConfigurationItem(textRes, iconRes, action))
    }
}

class MenuConfigurationItem(val textRes: Int, val iconRes: Int, val action: () -> Any)

inline fun createMenuConfiguration(function: MenuConfiguration.() -> Unit) = MenuConfiguration().apply(function)

fun Fragment.invalidateOptionsMenu() = requireActivity().invalidateOptionsMenu()

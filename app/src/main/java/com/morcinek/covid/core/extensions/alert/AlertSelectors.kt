package com.morcinek.covid.core.extensions.alert

import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.Fragment

fun Fragment.singleChoiceSelector(
    title: Int? = null,
    selectedItem: CharSequence? = null,
    items: List<CharSequence>,
    onClick: (DialogInterface, Int) -> Unit
): Unit = requireContext().selector(title?.let { getString(it) }, selectedItem, items, onClick)

fun Fragment.selector(
    title: Int? = null,
    items: List<CharSequence>,
    onClick: (DialogInterface, Int) -> Unit
): Unit = requireContext().selector(title?.let { getString(it) }, null, items, onClick)

fun Context.selector(
    title: CharSequence? = null,
    selectedItem: CharSequence? = null,
    items: List<CharSequence>,
    onClick: (DialogInterface, Int) -> Unit
) {
    with(AndroidAlertBuilder(this)) {
        if (title != null) {
            this.title = title
        }
        if (selectedItem != null) {
            singleChoiceItems(items, selectedItem, onClick)
        } else {
            items(items, onClick)
        }
        show()
    }
}
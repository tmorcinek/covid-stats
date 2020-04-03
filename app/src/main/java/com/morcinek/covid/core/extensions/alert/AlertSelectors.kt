package com.morcinek.covid.core.extensions.alert

import android.content.Context
import android.content.DialogInterface
import androidx.fragment.app.Fragment

fun Fragment.selector(
    title: CharSequence? = null,
    items: List<CharSequence>,
    onClick: (DialogInterface, Int) -> Unit
): Unit = requireContext().selector(title, items, onClick)

fun Fragment.selector(
    title: Int? = null,
    items: List<CharSequence>,
    onClick: (DialogInterface, Int) -> Unit
): Unit = requireContext().selector(title?.let { getString(it) }, items, onClick)

fun Context.selector(
    title: CharSequence? = null,
    items: List<CharSequence>,
    onClick: (DialogInterface, Int) -> Unit
) {
    with(AndroidAlertBuilder(this)) {
        if (title != null) {
            this.title = title
        }
        items(items, onClick)
        show()
    }
}
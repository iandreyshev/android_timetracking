package ru.iandreyshev.timemanager.utils

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu

fun AlertDialog?.dismissOnDestroy() {
    this?.setOnDismissListener(null)
    this?.dismiss()
}

fun PopupMenu?.dismissOnDestroy() {
    this?.setOnDismissListener(null)
    this?.dismiss()
}

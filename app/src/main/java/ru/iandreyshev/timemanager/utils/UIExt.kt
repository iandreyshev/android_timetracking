package ru.iandreyshev.timemanager.utils

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.bottomsheet.BottomSheetDialog

fun BottomSheetDialog?.dismissOnDestroy() {
    this?.setOnDismissListener(null)
    this?.dismiss()
}

fun AlertDialog?.dismissOnDestroy() {
    this?.setOnDismissListener(null)
    this?.dismiss()
}

fun PopupMenu?.dismissOnDestroy() {
    this?.setOnDismissListener(null)
    this?.dismiss()
}

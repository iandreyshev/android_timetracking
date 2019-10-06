package ru.iandreyshev.timemanager.ui.extensions

import android.content.res.Resources
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.R

fun Resources.getCardTitle(date: ZonedDateTime, repeatIndex: Int): String =
    when {
        repeatIndex > 0 ->
            getString(
                R.string.card_title_with_suffix,
                date.formatDate(),
                repeatIndex
            )
        else ->
            getString(
                R.string.card_title,
                date.formatDate()
            )
    }

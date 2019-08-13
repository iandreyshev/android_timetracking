package ru.iandreyshev.timemanager.ui.extensions

import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

fun ZonedDateTime.asUserReadableDate(from: ZonedDateTime): String {
    return format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}

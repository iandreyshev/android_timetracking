package ru.iandreyshev.timemanager.ui.timer

import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle

fun ZonedDateTime.asUserReadableDate(currentDate: ZonedDateTime): String {
    return format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}

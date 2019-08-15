package ru.iandreyshev.timemanager.ui.extensions

import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import ru.iandreyshev.timemanager.domain.Card
import java.util.*

fun Card.getTitleViewState(): String {
    return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM))
}

fun Date.hour() =
    ZonedDateTime.from(DateTimeUtils.toInstant(this)).hour

fun Date.minute() =
    ZonedDateTime.from(DateTimeUtils.toInstant(this)).minute

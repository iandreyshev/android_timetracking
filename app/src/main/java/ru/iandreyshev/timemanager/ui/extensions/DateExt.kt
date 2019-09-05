package ru.iandreyshev.timemanager.ui.extensions

import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.domain.Card
import java.util.*

fun Card.getTitleViewState(): String {
    return title
}

fun Date.hour() =
    ZonedDateTime.from(DateTimeUtils.toInstant(this)).hour

fun Date.minute() =
    ZonedDateTime.from(DateTimeUtils.toInstant(this)).minute

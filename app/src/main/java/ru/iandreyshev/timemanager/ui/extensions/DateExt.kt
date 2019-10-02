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

infix fun ZonedDateTime.withTime(time: ZonedDateTime) =
    withHour(time.hour)
        .withMinute(time.minute)
        .withSecond(time.second)

infix fun ZonedDateTime.sameDateWith(date: ZonedDateTime) =
    year == date.year && month == date.month && dayOfMonth == date.dayOfMonth

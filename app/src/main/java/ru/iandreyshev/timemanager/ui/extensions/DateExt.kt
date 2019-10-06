package ru.iandreyshev.timemanager.ui.extensions

import android.content.res.Resources
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.domain.Card
import java.util.*

private const val MINUTES_PER_HOUR = 60

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

infix fun ZonedDateTime.sameDateWith(otherDateTime: ZonedDateTime) =
    year == otherDateTime.year
            && month == otherDateTime.month
            && dayOfMonth == otherDateTime.dayOfMonth

fun Int.asTimerTitleViewState(resources: Resources): String {
    val hours = this / MINUTES_PER_HOUR
    val minutes = this % MINUTES_PER_HOUR

    return when {
        hours == 0 -> resources.getString(R.string.timer_title_format_minutes, minutes)
        minutes == 0 -> resources.getString(R.string.timer_title_format_hours, hours)
        else -> resources.getString(R.string.timer_title_format_hours_and_minutes, hours, minutes)
    }
}

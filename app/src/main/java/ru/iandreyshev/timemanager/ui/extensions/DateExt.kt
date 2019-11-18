package ru.iandreyshev.timemanager.ui.extensions

import android.content.res.Resources
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.ZonedDateTime
import org.threeten.bp.format.TextStyle
import ru.iandreyshev.timemanager.R
import java.util.*

private const val MINUTES_PER_HOUR = 60

fun Date.hour() =
    ZonedDateTime.from(DateTimeUtils.toInstant(this)).hour

fun Date.minute() =
    ZonedDateTime.from(DateTimeUtils.toInstant(this)).minute

fun ZonedDateTime.formatDate() =
    "$dayOfMonth ${month.getDisplayName(TextStyle.SHORT, Locale.getDefault())} $year"

fun ZonedDateTime.formatDate2() =
    "${dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())}, $dayOfMonth ${month.getDisplayName(TextStyle.SHORT, Locale.getDefault())}"

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

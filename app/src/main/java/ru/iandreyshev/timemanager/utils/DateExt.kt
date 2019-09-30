package ru.iandreyshev.timemanager.utils

import org.threeten.bp.ZonedDateTime

infix fun ZonedDateTime.withTime(time: ZonedDateTime) =
    withHour(time.hour)
        .withMinute(time.minute)
        .withSecond(time.second)

infix fun ZonedDateTime.sameDateWith(date: ZonedDateTime) =
    year == date.year && month == date.month && dayOfMonth == date.dayOfMonth

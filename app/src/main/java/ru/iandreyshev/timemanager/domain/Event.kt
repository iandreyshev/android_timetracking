package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

data class Event(
    val id: EventId,
    val description: String,
    val startDateTime: ZonedDateTime,
    val endDateTime: ZonedDateTime
) {

    fun getSpendMinutes(): Long {
        return (endDateTime.toEpochSecond() - startDateTime.toEpochSecond()) / 60
    }

}

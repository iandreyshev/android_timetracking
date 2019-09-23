package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

data class Event(
    val id: EventId,
    val description: String,
    val startTime: ZonedDateTime,
    val endTime: ZonedDateTime
) {

    fun getSpendMinutes(): Long {
        return (endTime.toEpochSecond() - startTime.toEpochSecond()) / 60
    }

}

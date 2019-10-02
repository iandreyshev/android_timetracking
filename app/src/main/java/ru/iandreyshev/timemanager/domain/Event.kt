package ru.iandreyshev.timemanager.domain

import org.threeten.bp.Duration
import org.threeten.bp.ZonedDateTime

data class Event(
    val id: EventId,
    val description: String,
    val startDateTime: ZonedDateTime,
    val endDateTime: ZonedDateTime
) {

    fun getDurationInMinutes(): Int =
        Duration.between(startDateTime, endDateTime).toMinutes().toInt()

}

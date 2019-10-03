package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.utils.betweenWithSecondsRounding

data class Event(
    val id: EventId,
    val description: String,
    val startDateTime: ZonedDateTime,
    val endDateTime: ZonedDateTime,
    val isFirstInCard: Boolean
) {

    fun getDurationInMinutes(): Int =
        betweenWithSecondsRounding(startDateTime, endDateTime)

}

package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

data class Event(
    val id: EventId,
    val description: String,
    val endTime: ZonedDateTime
)

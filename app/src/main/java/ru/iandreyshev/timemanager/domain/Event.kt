package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

class Event(
    val id: EventId,
    val title: String,
    val time: ZonedDateTime
)

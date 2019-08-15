package ru.iandreyshev.timemanager.domain

class Event(
    val id: EventId,
    val title: String,
    val epochTime: Long,
    val zoneId: String
)

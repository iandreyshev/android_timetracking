package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

interface IEventsRepo {
    fun list(date: ZonedDateTime): List<Event>
    fun update(event: Event)
}

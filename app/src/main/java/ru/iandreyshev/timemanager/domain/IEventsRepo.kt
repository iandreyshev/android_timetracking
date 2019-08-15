package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

interface IEventsRepo {
    suspend fun createCard(card: Card): Card
    suspend fun createEvent(card: Card, event: Event): Event

    suspend fun update(event: Event)

    suspend fun getEvents(card: Card): List<Event>
    fun getActualCard(currentDate: ZonedDateTime): Card?
    fun getNextCard(current: Card): Card?
    fun getPreviousCard(current: Card): Card?

    fun hasCards(): Boolean

    fun onEventUpdated(action: suspend () -> Unit)
}

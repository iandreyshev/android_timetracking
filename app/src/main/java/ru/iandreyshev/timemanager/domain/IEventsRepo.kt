package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

interface IEventsRepo {
    suspend fun createCard(card: Card): Card
    suspend fun createEvent(cardId: CardId, event: Event): Event?

    suspend fun update(cardId: CardId, event: Event)

    suspend fun getEvent(id: EventId): Event?
    suspend fun getEvents(card: Card): List<Event>
    suspend fun getActualCard(currentDate: ZonedDateTime): Card?
    suspend fun getNextCard(current: Card): Card?
    suspend fun getPreviousCard(current: Card): Card?
}

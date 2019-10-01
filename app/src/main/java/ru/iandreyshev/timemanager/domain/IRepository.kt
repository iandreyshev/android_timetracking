package ru.iandreyshev.timemanager.domain

interface IRepository {
    suspend fun saveCard(card: Card): Card
    suspend fun saveEvent(cardId: CardId, event: Event): RepoResult<Event>

    suspend fun update(cardId: CardId, event: Event): RepoResult<Unit>

    suspend fun getEvent(id: EventId): Event?
    suspend fun getEvents(cardId: CardId): List<Event>
    suspend fun getEventsCount(cardId: CardId): Int
    suspend fun getLastCard(): Card?
    suspend fun getNextCard(current: Card): Card?
    suspend fun getPreviousCard(current: Card): Card?

    suspend fun deleteCard(cardId: CardId): RepoResult<Unit>
}

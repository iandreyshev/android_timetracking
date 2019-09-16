package ru.iandreyshev.timemanager.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.domain.*

class Repository(
    private val cardDao: ICardDao,
    private val eventDao: IEventDao
) : IRepository {

    override suspend fun saveCard(card: Card): Card {
        return withContext(Dispatchers.Default) {
            val order = (cardDao.lastOrder() ?: 0) + 1
            val entity = CardEntity(
                title = "Card #$order",
                order = order
            )
            val id = cardDao.insert(entity)

            Card(
                id =  CardId(id),
                title = entity.title,
                date = ZonedDateTime.now()
            )
        }
    }

    override suspend fun saveEvent(cardId: CardId, event: Event): Event? {
        return withContext(Dispatchers.Default) {
            cardDao.get(cardId.value) ?: return@withContext null

            val entity = EventEntity.create(cardId, event)
            val id = eventDao.insert(entity)

            event.copy(id = EventId(id))
        }
    }

    override suspend fun update(cardId: CardId, event: Event) {
        withContext(Dispatchers.Default) {
            val cardEntity = cardDao.get(cardId.value) ?: return@withContext
            eventDao.update(EventEntity.create(cardEntity.id, event))
        }
    }

    override suspend fun getEvent(id: EventId): Event? {
        return withContext(Dispatchers.Default) {
            val entity = eventDao.get(id.value) ?: return@withContext null

            Event(
                id = EventId(entity.id),
                description = entity.description,
                endTime = entity.endTime
            )
        }
    }

    override suspend fun getEvents(card: Card): List<Event> {
        return withContext(Dispatchers.Default) {
            eventDao.getAll(card.id.value)
                .map { entity ->
                    Event(
                        id = EventId(entity.id),
                        description = entity.description,
                        endTime = entity.endTime
                    )
                }
        }
    }

    override suspend fun getNextCard(current: Card): Card? {
        return withContext(Dispatchers.Default) {
            val entity = cardDao.get(current.id.value) ?: return@withContext null
            val next = cardDao.getNext(entity.order) ?: return@withContext null

            Card(
                id = CardId(next.id),
                title = next.title,
                date = ZonedDateTime.now()
            )
        }
    }

    override suspend fun getPreviousCard(current: Card): Card? {
        return withContext(Dispatchers.Default) {
            val entity = cardDao.get(current.id.value) ?: return@withContext null
            val previous = cardDao.getPrevious(entity.order) ?: return@withContext null

            Card(
                id = CardId(previous.id),
                title = previous.title,
                date = ZonedDateTime.now()
            )
        }
    }

    override suspend fun getLastCard(): Card? {
        return withContext(Dispatchers.Default) {
            val entity = cardDao.getLast() ?: return@withContext null

            Card(
                id = CardId(entity.id),
                title = entity.title,
                date = ZonedDateTime.now()
            )
        }
    }

}

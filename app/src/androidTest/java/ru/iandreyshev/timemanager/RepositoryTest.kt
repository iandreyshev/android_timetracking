package ru.iandreyshev.timemanager

import androidx.room.Room
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.repository.cards.AppDatabase
import ru.iandreyshev.timemanager.repository.cards.ICardDao
import ru.iandreyshev.timemanager.repository.cards.IEventDao

@SmallTest
class RepositoryTest {

    private lateinit var mDatabase: AppDatabase
    private lateinit var cardDao: ICardDao
    private lateinit var eventDao: IEventDao

    @Before
    @Throws(Exception::class)
    fun createDb() {
        mDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        ).build()

        cardDao = mDatabase.cardDao()
        eventDao = mDatabase.eventDao()
    }

    @After
    @Throws(Exception::class)
    fun closeDb() {
        mDatabase.close()
    }

    @Test
    fun can_get_card_from_memory() {
        val entity = CardEntity(
            id = 0,
            title = "Card title",
            order = 0
        )

        val id = cardDao.insert(entity)
        entity.id = id
        val entityFromDao = cardDao.get(id)

        assertEquals(entity, entityFromDao)
    }

    @Test
    fun can_return_last_card() {
        var lastOrder: Long = 0

        repeat(100) { order ->
            val card = CardEntity(
                id = 0,
                title = "Card #$order",
                order = order.toLong()
            )

            lastOrder = order.toLong()
            cardDao.insert(card)
        }

        val lastCard = cardDao.getLast()

        assertEquals(lastCard?.order, lastOrder)
    }

    @Test
    fun return_last_as_null_by_default() {
        val last = cardDao.getLast()

        assertNull(last)
    }

    @Test
    fun return_empty_list_with_events_by_default() {
        val card = CardEntity(
            id = 0,
            title = "Card without events",
            order = 0
        )
        val cardId = cardDao.insert(card)

        assertTrue(eventDao.getAll(cardId).isEmpty())
    }

    @Test
    fun can_return_events_for_card() {
        val card = CardEntity(
            id = 0,
            title = "Card with events",
            order = 0
        )
        val cardId = cardDao.insert(card)
        val eventsCount = 100

        repeat(eventsCount) {
            eventDao.insert(
                EventEntity(
                    id = 0,
                    cardId = cardId,
                    description = "Event #$it description",
                    endTime = ZonedDateTime.now()
                )
            )
        }

        val eventsFromDao = eventDao.getAll(cardId)

        assertEquals(eventsFromDao.count(), eventsCount)
    }

    @Test
    fun return_null_when_next_card_not_exists() {
        val order: Long = 2
        val card = CardEntity(
            id = 0,
            title = "Card without next",
            order = order
        )

        cardDao.insert(card)

        val next = cardDao.getNext(order)

        assertNull(next)
    }

    @Test
    fun can_return_next_card_when_has_one() {
        val order: Long = 2
        val card = CardEntity(
            id = 0,
            title = "Card with one next",
            order = order
        )
        cardDao.insert(card)

        val nextOrder = order + 1
        val nextCard = CardEntity(
            id = 0,
            title = "Next card",
            order = nextOrder
        )
        cardDao.insert(nextCard)

        val next = cardDao.getNext(order)

        assertEquals(next?.order, nextOrder)
    }

    @Test
    fun can_return_next_card_when_has_some() {
        val cardsCount: Long = 100
        var orderToInsertNext: Long = 1

        val firstCard = CardEntity(
            id = 0,
            title = "Card with some next",
            order = orderToInsertNext
        )
        cardDao.insert(firstCard)

        repeat(cardsCount.toInt()) {
            val nextCard = CardEntity(
                id = 0,
                title = "Next card",
                order = ++orderToInsertNext
            )
            cardDao.insert(nextCard)
        }

        var orderToGetNext: Long = 1

        repeat(cardsCount.toInt()) {
            val next = cardDao.getNext(orderToGetNext++)
            assertEquals(orderToGetNext, next?.order)
        }
    }

    @Test
    fun return_null_when_previous_card_not_exists() {
        val order: Long = 2
        val card = CardEntity(
            id = 0,
            title = "Card without next",
            order = order
        )

        cardDao.insert(card)

        val next = cardDao.getPrevious(order)

        assertNull(next)
    }

    @Test
    fun can_return_previous_card_when_has_one() {
        val order: Long = 2
        val card = CardEntity(
            id = 0,
            title = "Card with one next",
            order = order
        )
        cardDao.insert(card)

        val previousOrder = order - 1
        val previousCard = CardEntity(
            id = 0,
            title = "Next card",
            order = previousOrder
        )
        cardDao.insert(previousCard)

        val next = cardDao.getPrevious(order)

        assertEquals(next?.order, previousOrder)
    }

    @Test
    fun can_return_previous_card_when_has_some() {
        val cardsCount: Long = 100
        var orderToInsertPrevious: Long = cardsCount

        val firstCard =
            CardEntity(
                id = 0,
                title = "Card with some previous",
                order = orderToInsertPrevious
            )
        cardDao.insert(firstCard)

        repeat(cardsCount.toInt()) {
            val previousCard =
                CardEntity(
                    id = 0,
                    title = "Previous card",
                    order = --orderToInsertPrevious
                )
            cardDao.insert(previousCard)
        }

        var orderToGetPrevious = cardsCount

        repeat(cardsCount.toInt()) {
            val previous = cardDao.getPrevious(orderToGetPrevious--)
            assertEquals(orderToGetPrevious, previous?.order)
        }
    }

    @Test
    fun return_zero_cards_count_by_default() {
        assertEquals(cardDao.count(), 0)
    }

    @Test
    fun can_return_cards_count() {
        val cardsCount = 123

        repeat(cardsCount) {
            val order = it.toLong()
            val card = CardEntity(
                id = 0,
                title = "Card #$it",
                order = order
            )
            cardDao.insert(card)
        }

        assertEquals(cardDao.count(), cardsCount)
    }

}

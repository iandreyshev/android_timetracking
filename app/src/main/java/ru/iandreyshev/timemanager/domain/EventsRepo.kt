package ru.iandreyshev.timemanager.domain

import kotlinx.coroutines.delay
import org.threeten.bp.ZonedDateTime

class EventsRepo : IEventsRepo {

    private val mMemory = sortedMapOf<Card, MutableList<Event>>()
    private var mOnDataUpdated = suspend {}

    override suspend fun createCard(card: Card): Card {
        mMemory[card] = mutableListOf()
        mOnDataUpdated()
        return card
    }

    override suspend fun createEvent(cardId: CardId, event: Event): Event? {
        val key = mMemory.keys.find { it.id == cardId } ?: return null
        mMemory[key]?.add(event)
        mOnDataUpdated()
        return event
    }

    override suspend fun update(event: Event) {
        val key =
            mMemory.filterValues { it.firstOrNull { listEvent -> listEvent.id == event.id } != null }
                .keys
                .firstOrNull() ?: return
        val listWithEvent = mMemory[key]
        val eventIndex = listWithEvent?.indexOfFirst { it.id == event.id }
            ?: return

        listWithEvent[eventIndex] = event
        mOnDataUpdated()
    }

    override suspend fun getEvents(card: Card): List<Event> {
        delay(1500)
        return mMemory[card].orEmpty()
    }

    override fun getNextCard(current: Card): Card? {
        return null
    }

    override fun getPreviousCard(current: Card): Card? {
        return null
    }

    override fun hasCards(): Boolean {
        return mMemory.isNotEmpty()
    }

    override fun getActualCard(currentDate: ZonedDateTime): Card? {
        return null
    }

    override fun onEventUpdated(action: suspend () -> Unit) {
        mOnDataUpdated = action
    }

}
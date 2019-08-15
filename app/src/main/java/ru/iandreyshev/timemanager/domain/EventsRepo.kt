package ru.iandreyshev.timemanager.domain

import kotlinx.coroutines.delay
import org.threeten.bp.ZonedDateTime

class EventsRepo : IEventsRepo {

    private val mMemory = sortedMapOf<Card, MutableList<Event>>()
    private var mOnDataUpdated = suspend {}

    override suspend fun update(event: Event) {
        TODO("not implemented") //To change body of created suspend functions use File | Settings | File Templates.
    }

    override suspend fun createCard(card: Card): Card {
        mMemory[card] = mutableListOf()
        return card
    }

    override suspend fun createEvent(card: Card, event: Event): Event {
        if (!mMemory.containsKey(card)) {
            mMemory[card] = mutableListOf()
        }

        mMemory[card]?.add(event)

        return event
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
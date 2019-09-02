package ru.iandreyshev.timemanager.repository

import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.domain.*
import timber.log.Timber

class EventsRepo : IEventsRepo {

    private val mMemory = sortedMapOf<Card, MutableList<Event>>()
    private var mOnDataUpdated = suspend {}
    private var mEventIds = 1L

    override suspend fun createCard(card: Card): Card {
        Timber.d("Create card")
        mMemory[card] = mutableListOf()
        mOnDataUpdated()
        return card
    }

    override suspend fun createEvent(cardId: CardId, event: Event): Event? {
        Timber.d("Create event")
        val key = mMemory.keys.find { it.id == cardId } ?: return null
        val eventToSave = Event(EventId(mEventIds++), event.title, event.endTime)
        mMemory[key]?.add(eventToSave)
        mOnDataUpdated()
        return eventToSave
    }

    override suspend fun update(event: Event) {
        Timber.d("Update")
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

    override suspend fun getEvent(id: EventId): Event? {
        val key = mMemory.keys.firstOrNull { key ->
            mMemory[key]?.find { event ->
                event.id == id
            } != null
        } ?: return null

        return mMemory[key]?.firstOrNull { it.id == id }
    }

    override suspend fun getEvents(card: Card): List<Event> {
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
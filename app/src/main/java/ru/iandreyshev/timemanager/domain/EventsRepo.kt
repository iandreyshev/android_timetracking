package ru.iandreyshev.timemanager.domain

import org.threeten.bp.ZonedDateTime

class EventsRepo : IEventsRepo {

    override fun update(event: Event) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun list(date: ZonedDateTime): List<Event> {
        return emptyList()
    }

}
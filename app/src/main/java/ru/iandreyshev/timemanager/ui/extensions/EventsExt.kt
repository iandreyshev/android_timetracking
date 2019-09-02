package ru.iandreyshev.timemanager.ui.extensions

import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.timemanager.domain.Event
import ru.iandreyshev.timemanager.ui.timeline.EventViewState

fun List<Event>.asViewState(): List<EventViewState> {
    return map { item ->
        EventViewState(item.id, item.title, item.endTime.format(DateTimeFormatter.ISO_DATE))
    }
}

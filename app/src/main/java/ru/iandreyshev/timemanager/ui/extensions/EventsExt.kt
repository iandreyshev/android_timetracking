package ru.iandreyshev.timemanager.ui.extensions

import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.timemanager.domain.Event
import ru.iandreyshev.timemanager.ui.timeline.EventViewState

private val END_DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")

fun List<Event>?.asViewState(): List<EventViewState> {
    this ?: return listOf()
    return map { item ->
        EventViewState(
            id = item.id,
            title = item.description,
            endTime = item.endTime.format(END_DATE_FORMATTER)
        )
    }
}

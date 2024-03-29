package ru.iandreyshev.timemanager.ui.extensions

import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.timemanager.domain.cards.Event
import ru.iandreyshev.timemanager.ui.timeline.EventSelectionViewState
import ru.iandreyshev.timemanager.ui.timeline.EventViewState

internal val START_DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
internal val END_DATE_FORMATTER = START_DATE_FORMATTER

fun List<Event>?.asViewState(): List<EventViewState> {
    this ?: return listOf()

    return mapIndexed { index, event ->
        EventViewState(
            id = event.id,
            title = event.description,
            startTime = event.startDateTime.format(END_DATE_FORMATTER),
            endTime = event.endDateTime.format(END_DATE_FORMATTER),
            isMiddleEndTime = count() != 1 && index != 0,
            durationInMinutes = event.getDurationInMinutes(),
            selection = EventSelectionViewState.Normal
        )
    }
}

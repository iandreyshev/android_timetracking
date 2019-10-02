package ru.iandreyshev.timemanager.ui.extensions

import org.threeten.bp.format.DateTimeFormatter
import ru.iandreyshev.timemanager.domain.Event
import ru.iandreyshev.timemanager.ui.timeline.EventSelectionViewState
import ru.iandreyshev.timemanager.ui.timeline.EventViewState

private val START_DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
private val END_DATE_FORMATTER = START_DATE_FORMATTER

fun List<Event>?.asViewState(): List<EventViewState> {
    this ?: return listOf()

    return mapIndexed { index, event ->
        val startTime =
                if (index == 0) null
                else event.startDateTime.format(END_DATE_FORMATTER)

        EventViewState(
                id = event.id,
                title = event.description,
                startTime = startTime,
                endTime = event.endDateTime.format(END_DATE_FORMATTER),
                durationInMinutes = event.getDurationInMinutes(),
                selection = EventSelectionViewState.Normal
        )
    }
}

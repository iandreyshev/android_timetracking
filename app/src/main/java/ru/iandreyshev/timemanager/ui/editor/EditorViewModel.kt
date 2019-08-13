package ru.iandreyshev.timemanager.ui.editor

import androidx.lifecycle.ViewModel
import org.threeten.bp.ZonedDateTime
import ru.iandreyshev.timemanager.domain.Event
import ru.iandreyshev.timemanager.domain.EventId
import ru.iandreyshev.timemanager.domain.IEventsRepo

class EditorViewModel(
    private val date: ZonedDateTime,
    private val eventToEdit: Event?,
    private val eventsRepo: IEventsRepo
) : ViewModel() {

    fun onSave(editorEvent: EditorEvent) {
        eventsRepo.update(
            Event(
                id = eventToEdit?.id ?: EventId.undefined(),
                title = editorEvent.title,
                time = date.withHour(editorEvent.hour)
                    .withMinute(editorEvent.minute)
            )
        )
    }

}

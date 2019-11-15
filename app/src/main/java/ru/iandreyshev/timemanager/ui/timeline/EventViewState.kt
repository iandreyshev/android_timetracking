package ru.iandreyshev.timemanager.ui.timeline

import ru.iandreyshev.timemanager.domain.cards.EventId

class EventViewState(
    val id: EventId,
    val title: String,
    val startTime: String,
    val endTime: String,
    val isMiddleEndTime: Boolean,
    var durationInMinutes: Int,
    var selection: EventSelectionViewState
)

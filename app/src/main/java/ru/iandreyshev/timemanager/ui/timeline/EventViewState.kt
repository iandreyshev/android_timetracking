package ru.iandreyshev.timemanager.ui.timeline

import ru.iandreyshev.timemanager.domain.EventId

class EventViewState(
    val id: EventId,
    val title: String,
    val startTime: String?,
    val endTime: String,
    var duration: String,
    var selection: EventSelectionViewState
)

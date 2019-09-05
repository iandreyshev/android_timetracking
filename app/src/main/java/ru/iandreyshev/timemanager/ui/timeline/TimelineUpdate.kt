package ru.iandreyshev.timemanager.ui.timeline

sealed class TimelineUpdate {
    object CardCreated : TimelineUpdate()
}

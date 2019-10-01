package ru.iandreyshev.timemanager.ui.timeline

sealed class EventSelectionViewState {
    object Normal : EventSelectionViewState()
    object SelectedByTimer : EventSelectionViewState()
}

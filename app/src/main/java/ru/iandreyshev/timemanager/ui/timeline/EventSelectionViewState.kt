package ru.iandreyshev.timemanager.ui.timeline

sealed class EventSelectionViewState {
    object Normal : EventSelectionViewState()
    class TimerMode(val isSelected: Boolean) : EventSelectionViewState()
}

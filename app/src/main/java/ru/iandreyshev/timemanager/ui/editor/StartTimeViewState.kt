package ru.iandreyshev.timemanager.ui.editor

sealed class StartTimeViewState {
    object Hidden : StartTimeViewState()
    object Undefined : StartTimeViewState()
    class ShowTime(val value: String) : StartTimeViewState()
}

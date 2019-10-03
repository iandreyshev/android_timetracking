package ru.iandreyshev.timemanager.ui.editor

sealed class StartTimeViewState {
    object Undefined : StartTimeViewState()
    class ShowTime(val value: String) : StartTimeViewState()
}

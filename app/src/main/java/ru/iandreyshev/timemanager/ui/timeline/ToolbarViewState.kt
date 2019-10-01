package ru.iandreyshev.timemanager.ui.timeline

sealed class ToolbarViewState {
    object CardTitle : ToolbarViewState()
    class Timer(val minutes: Int) : ToolbarViewState()
}

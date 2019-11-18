package ru.iandreyshev.timemanager.ui.editor

sealed class StartDateViewState {
    class Today(val value: String) : StartDateViewState()
    class ShowDate(val value: String) : StartDateViewState()
}

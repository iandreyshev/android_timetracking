package ru.iandreyshev.timemanager.ui.editor

sealed class EndDateViewState {
    object Hidden : EndDateViewState()
    class Today(val value: String) : EndDateViewState()
    class ShowDate(val value: String) : EndDateViewState()
}

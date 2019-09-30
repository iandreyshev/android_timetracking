package ru.iandreyshev.timemanager.ui.editor

sealed class EndDateViewState {
    object Hidden : EndDateViewState()
    object Today : EndDateViewState()
    class ShowDate(val value: String) : EndDateViewState()
}

package ru.iandreyshev.timemanager.ui.editor

sealed class StartDateViewState {
    object Hidden : StartDateViewState()
    object Today : StartDateViewState()
    class ShowDate(val value: String) : StartDateViewState()
}

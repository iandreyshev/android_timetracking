package ru.iandreyshev.timemanager.ui.editor

sealed class InputValidationError {
    object EmptyText : InputValidationError()
    object ExpectedStartTime : InputValidationError()
    object EndBeforeStart : InputValidationError()
}

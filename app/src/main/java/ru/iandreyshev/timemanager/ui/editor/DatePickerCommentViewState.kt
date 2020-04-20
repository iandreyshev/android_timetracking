package ru.iandreyshev.timemanager.ui.editor

sealed class DatePickerCommentViewState {
    object Hidden : DatePickerCommentViewState()
    object JustNow : DatePickerCommentViewState()
    class RightAfter(val event: String) : DatePickerCommentViewState()
    class ErrorStartBeforePrevious(val event: String) : DatePickerCommentViewState()
    object ErrorEndBeforeStart : DatePickerCommentViewState()
}

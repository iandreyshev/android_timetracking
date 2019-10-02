package ru.iandreyshev.timemanager.ui.editor

import java.util.*

sealed class DatePickerViewState {
    class StartDate(val default: Date, val listener: (Date?) -> Unit) : DatePickerViewState()
    class StartTime(val default: Date, val listener: (Date?) -> Unit) : DatePickerViewState()
    class EndDate(val default: Date, val listener: (Date?) -> Unit) : DatePickerViewState()
    class EndTime(val default: Date, val listener: (Date?) -> Unit) : DatePickerViewState()
}

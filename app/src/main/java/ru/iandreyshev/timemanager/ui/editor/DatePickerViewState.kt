package ru.iandreyshev.timemanager.ui.editor

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import org.threeten.bp.ZonedDateTime

sealed class DatePickerViewState {

    object Hidden : DatePickerViewState()

    class StartDate(
        val date: ZonedDateTime,
        val listener: DatePickerDialog.OnDateSetListener
    ) : DatePickerViewState()

    class StartTime(
        val time: ZonedDateTime,
        val listener: TimePickerDialog.OnTimeSetListener
    ) : DatePickerViewState()

    class EndDate(
        val date: ZonedDateTime,
        val listener: DatePickerDialog.OnDateSetListener
    ) : DatePickerViewState()

    class EndTime(
        val time: ZonedDateTime,
        val listener: TimePickerDialog.OnTimeSetListener
    ) : DatePickerViewState()

}

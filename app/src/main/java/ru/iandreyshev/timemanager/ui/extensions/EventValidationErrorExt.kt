package ru.iandreyshev.timemanager.ui.extensions

import android.content.res.Resources
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.ui.editor.InputValidationError

fun InputValidationError.asText(resources: Resources): String =
    when (this) {
        InputValidationError.EmptyText -> resources.getString(R.string.event_validation_error_description_is_empty)
        InputValidationError.ExpectedStartTime -> resources.getString(R.string.event_validation_error_expected_start_time)
    }

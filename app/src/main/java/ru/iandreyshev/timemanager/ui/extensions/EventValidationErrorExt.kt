package ru.iandreyshev.timemanager.ui.extensions

import android.content.res.Resources
import ru.iandreyshev.timemanager.R
import ru.iandreyshev.timemanager.domain.validation.EventValidationError

fun EventValidationError.asText(resources: Resources): String =
    when (this) {
        EventValidationError.EmptyText -> resources.getString(R.string.event_validation_error_description_is_empty)
        EventValidationError.ExpectedEndDate -> resources.getString(R.string.event_validation_error_expected_end_time)
        EventValidationError.ExpectedStartDate -> resources.getString(R.string.event_validation_error_expected_start_time)
    }

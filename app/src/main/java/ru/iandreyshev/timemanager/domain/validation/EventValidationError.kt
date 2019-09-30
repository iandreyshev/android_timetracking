package ru.iandreyshev.timemanager.domain.validation

sealed class EventValidationError {
    object EmptyText : EventValidationError()
    object ExpectedEndDate : EventValidationError()
    object ExpectedStartDate : EventValidationError()
}

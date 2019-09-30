package ru.iandreyshev.timemanager.domain.validation

import ru.iandreyshev.timemanager.domain.Event

interface IEventValidator {
    fun validateEvent(event: Event): EventValidationError?
}

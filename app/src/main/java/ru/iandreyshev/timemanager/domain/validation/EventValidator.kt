package ru.iandreyshev.timemanager.domain.validation

import ru.iandreyshev.timemanager.domain.Event

class EventValidator : IEventValidator {

    override fun validateEvent(event: Event): EventValidationError? {
        if (event.description.isBlank()) {
            return EventValidationError.EmptyText
        }

        return null
    }

}

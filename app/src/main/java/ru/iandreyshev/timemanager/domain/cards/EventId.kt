package ru.iandreyshev.timemanager.domain.cards

inline class EventId(val value: Long) {

    companion object {
        fun default() = EventId(-1)
    }

}

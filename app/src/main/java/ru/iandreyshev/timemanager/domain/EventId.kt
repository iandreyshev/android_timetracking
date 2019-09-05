package ru.iandreyshev.timemanager.domain

inline class EventId(val value: Long) {

    companion object {
        fun default() = EventId(-1)
    }

}

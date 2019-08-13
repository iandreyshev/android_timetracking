package ru.iandreyshev.timemanager.domain

inline class EventId(val id: Long) {

    companion object {
        fun undefined() = EventId(-1)
    }

}

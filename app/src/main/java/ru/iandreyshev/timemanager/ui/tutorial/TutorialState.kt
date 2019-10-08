package ru.iandreyshev.timemanager.ui.tutorial

import androidx.annotation.IdRes
import ru.iandreyshev.timemanager.R

enum class TutorialState(@IdRes val id: Int) {
    UNDEFINED(-1),
    NO_CARD(R.id.noCard),
    EMPTY_CARD(R.id.emptyCard),
    ONE_EVENT(R.id.oneEvent),
    TWO_EVENTS(R.id.twoEvents),
    TWO_EVENTS_PAUSE(R.id.twoEventsPause),
    THREE_EVENTS(R.id.threeEvents),
    ONE_EVENT_SELECTED(R.id.oneEventSelected),
    TWO_EVENTS_SELECTED(R.id.twoEventsSelected),
    AWAIT_DONE(R.id.awaitDone),
    DONE(R.id.done);

    companion object {
        val start = NO_CARD

        fun from(id: Int) = values()
            .firstOrNull { it.id == id }
            ?: UNDEFINED
    }

}

package ru.iandreyshev.timemanager.ui.tutorial

import androidx.annotation.IdRes
import ru.iandreyshev.timemanager.R

enum class TutorialState(@IdRes val id: Int) {
    NO_CARD(R.id.noCard),
    EMPTY_CARD(R.id.emptyCard),
    ONE_EVENT(R.id.oneEvent),
    THREE_EVENTS(R.id.threeEvents);
}

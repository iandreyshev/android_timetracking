package ru.iandreyshev.timemanager.ui.timeline.state

import ru.iandreyshev.timemanager.domain.cards.Event

abstract class TimelineState {

    protected var context = ITimelineStateContext.newStub()
        private set

    fun setContext(context: ITimelineStateContext) {
        this.context = context
        onContextUpdated()
    }

    open fun onBackPressed() = true
    open fun onEventClick(position: Int) = Unit
    open fun onStartTimerMode(position: Int) = Unit
    open fun onEndTimerMode() = Unit
    open fun onEventsUpdated(events: List<Event>?) = Unit

    protected open fun onContextUpdated() = Unit

}

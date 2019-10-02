package ru.iandreyshev.timemanager.ui.timeline.state

import ru.iandreyshev.timemanager.ui.timeline.EventSelectionViewState
import ru.iandreyshev.timemanager.ui.timeline.ToolbarViewState

class NormalState : TimelineState() {

    override fun onContextUpdated() {
        context.updateToolbar(ToolbarViewState.CardTitle)
        context.updateAllEventsSelection(EventSelectionViewState.Normal)
        context.updateAddEventButton(true)
    }

    override fun onEventClick(position: Int) =
        context.openEvent(position)

    override fun onStartTimerMode(position: Int) {
        val state = TimerState(position)
        context.setState(state)
    }

    override fun onBackPressed() = false

}

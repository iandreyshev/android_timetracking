package ru.iandreyshev.timemanager.ui.timeline.state

import ru.iandreyshev.timemanager.ui.timeline.EventSelectionViewState
import ru.iandreyshev.timemanager.ui.timeline.ToolbarViewState

class TimerState(
        private var firstSelectedItemPosition: Int
) : TimelineState() {

    private var mSelectedMinutes = 0
    private var mEventsDuration = listOf<Int>()

    override fun onContextUpdated() {
        context.updateToolbar(ToolbarViewState.Timer(mSelectedMinutes))
        onEventClick(firstSelectedItemPosition)
    }

    override fun onEventClick(position: Int) {
        context.updateEventSelection(position, EventSelectionViewState.SelectedByTimer)
        context.updateToolbar(ToolbarViewState.Timer(mSelectedMinutes))
    }

    override fun onEndTimerMode() {
        context.setState(NormalState())
        mSelectedMinutes = 0
    }

    override fun onBackPressed(): Boolean {
        onEndTimerMode()
        return true
    }

}
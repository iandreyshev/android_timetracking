package ru.iandreyshev.timemanager.ui.timeline.state

import ru.iandreyshev.timemanager.domain.Event
import ru.iandreyshev.timemanager.ui.timeline.EventSelectionViewState
import ru.iandreyshev.timemanager.ui.timeline.ToolbarViewState

class TimerState(
    private var firstSelectedItemPosition: Int
) : TimelineState() {

    private var mSelectedMinutes = 0
    private var mSelectedPositions = mutableSetOf<Int>()
    private var mEventsDuration = mapOf<Int, Int>()

    override fun onContextUpdated() {
        context.updateToolbar(ToolbarViewState.Timer(mSelectedMinutes))
        context.updateAllEventsSelection(EventSelectionViewState.TimerMode(false))
        onEventClick(firstSelectedItemPosition)
        context.updateAddEventButton(false)
    }

    override fun onEventClick(position: Int) {
        val isSelected = !mSelectedPositions.contains(position)

        context.updateEventSelection(position, EventSelectionViewState.TimerMode(isSelected))

        if (isSelected) {
            mSelectedPositions.add(position)
        } else {
            mSelectedPositions.remove(position)
        }

        mSelectedMinutes = computeTime(mEventsDuration, mSelectedPositions)
        context.updateToolbar(ToolbarViewState.Timer(mSelectedMinutes))
    }

    override fun onEventsUpdated(events: List<Event>?) {
        var position = 0
        mEventsDuration = events.orEmpty().associate { event ->
            position++ to event.getDurationInMinutes()
        }
    }

    override fun onEndTimerMode() {
        context.setState(NormalState())
        mSelectedMinutes = 0
    }

    override fun onBackPressed(): Boolean {
        onEndTimerMode()
        return true
    }

    companion object {
        private fun computeTime(durations: Map<Int, Int>, positions: Set<Int>): Int =
            positions.fold(0) { acc, i ->
                val duration = durations[i] ?: return@fold acc
                return@fold acc + duration
            }
    }

}
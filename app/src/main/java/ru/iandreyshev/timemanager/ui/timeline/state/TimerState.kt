package ru.iandreyshev.timemanager.ui.timeline.state

import org.threeten.bp.Duration
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
        onEventClick(firstSelectedItemPosition)
    }

    override fun onEventClick(position: Int) {
        val isSelect = mSelectedPositions.contains(position)

        if (isSelect) {
            context.updateEventSelection(position, EventSelectionViewState.SelectedByTimer)
            mSelectedPositions.add(position)
        } else {
            context.updateEventSelection(position, EventSelectionViewState.Normal)
            mSelectedPositions.remove(position)
        }

        mSelectedMinutes = computeTime(mEventsDuration, mSelectedPositions)
        context.updateToolbar(ToolbarViewState.Timer(mSelectedMinutes))
    }

    override fun onEventsUpdated(events: List<Event>) {
        var position = 0
        mEventsDuration = events.associate { event ->
            val duration = Duration.between(event.startDateTime, event.endDateTime)
                .toMinutes()
                .toInt()

            return@associate position++ to duration
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
package ru.iandreyshev.timemanager.ui.timeline.state

import ru.iandreyshev.timemanager.ui.timeline.EventSelectionViewState
import ru.iandreyshev.timemanager.ui.timeline.ToolbarViewState

interface ITimelineStateContext {
    fun setState(state: TimelineState)
    fun openEvent(position: Int)
    fun updateToolbar(viewState: ToolbarViewState)
    fun updateEventSelection(position: Int, viewState: EventSelectionViewState)
    fun updateAllEventsSelection(viewState: EventSelectionViewState)
    fun updateAddEventButton(isVisible: Boolean)

    companion object {
        fun newStub() = object : ITimelineStateContext {
            override fun setState(state: TimelineState) = Unit
            override fun openEvent(position: Int) = Unit
            override fun updateToolbar(viewState: ToolbarViewState) = Unit
            override fun updateEventSelection(position: Int, viewState: EventSelectionViewState) = Unit
            override fun updateAllEventsSelection(viewState: EventSelectionViewState) = Unit
            override fun updateAddEventButton(isVisible: Boolean) = Unit
        }
    }

}

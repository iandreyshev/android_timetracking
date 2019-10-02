package ru.iandreyshev.timemanager.ui.timeline

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_timeline_event.view.*
import ru.iandreyshev.timemanager.ui.extensions.asTimerTitleViewState
import ru.iandreyshev.timemanager.utils.exhaustive

class EventViewHolder(
    view: View,
    onClickListener: (EventViewHolder) -> Unit,
    onLongClickListener: (EventViewHolder) -> Boolean
) : RecyclerView.ViewHolder(view) {

    init {
        itemView.clickableArea.setOnClickListener { onClickListener(this) }
        itemView.clickableArea.setOnLongClickListener { onLongClickListener(this) }
    }

    private val mViewsToBlurOnSelect = listOf(
        itemView.title,
        itemView.startTimeIcon,
        itemView.startTime,
        itemView.endTimeIcon,
        itemView.endTime
    )

    fun bind(viewState: EventViewState) = with(itemView) {
        title.text = viewState.title

        val hasStartTime = !viewState.startTime.isNullOrBlank()
        startTimeIcon.isVisible = hasStartTime
        startTime.isVisible = hasStartTime
        startTime.text = viewState.startTime.orEmpty()

        val duration = viewState.durationInMinutes.asTimerTitleViewState(resources)
        endTime.text = "${viewState.endTime} ($duration)"

        when (val selectionViewState = viewState.selection) {
            EventSelectionViewState.Normal ->
                mViewsToBlurOnSelect.forEach { it.alpha = NORMAL_ALPHA }
            is EventSelectionViewState.TimerMode -> {
                if (selectionViewState.isSelected) {
                    mViewsToBlurOnSelect.forEach { it.alpha = TIMER_MODE_SELECTED_ALPHA }
                } else {
                    mViewsToBlurOnSelect.forEach { it.alpha = TIMER_MODE_NORMAL_ALPHA }
                }
            }
        }.exhaustive
    }

    companion object {
        private const val NORMAL_ALPHA = 1f
        private const val TIMER_MODE_SELECTED_ALPHA = 1f
        private const val TIMER_MODE_NORMAL_ALPHA = 0.42f
    }

}

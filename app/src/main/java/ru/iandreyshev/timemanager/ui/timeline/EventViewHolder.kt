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
    onLongClickListener: (EventViewHolder) -> Boolean,
    private val onOptionsClick: ((EventViewHolder) -> Unit)? = null
) : RecyclerView.ViewHolder(view) {

    init {
        itemView.clickableArea.setOnClickListener { onClickListener(this) }
        itemView.clickableArea.setOnLongClickListener { onLongClickListener(this) }
        itemView.optionsButton.setOnClickListener { onOptionsClick?.invoke(this) }
    }

    private val mViewsToBlurOnSelect = listOf(
        itemView.title,
        itemView.startTimeIcon,
        itemView.time
    )

    fun bind(viewState: EventViewState) = with(itemView) {
        title.text = viewState.title

        val duration = viewState.durationInMinutes.asTimerTitleViewState(resources)
        time.text = "${viewState.startTime} - ${viewState.endTime} ($duration)"

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

        itemView.optionsButton.isVisible = onOptionsClick != null
    }

    companion object {
        private const val NORMAL_ALPHA = 1f
        private const val TIMER_MODE_SELECTED_ALPHA = 1f
        private const val TIMER_MODE_NORMAL_ALPHA = 0.42f
    }

}

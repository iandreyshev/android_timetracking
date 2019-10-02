package ru.iandreyshev.timemanager.ui.timeline

import android.view.View
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_timeline_event.view.*
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

    fun bind(viewState: EventViewState) {
        itemView.title.text = viewState.title

        val hasStartTime = !viewState.startTime.isNullOrBlank()
        itemView.startTimeIcon.isVisible = hasStartTime
        itemView.startTime.isVisible = hasStartTime
        itemView.startTime.text = viewState.startTime.orEmpty()

        itemView.endTime.text = "${viewState.endTime} (${viewState.duration})"
    }

}

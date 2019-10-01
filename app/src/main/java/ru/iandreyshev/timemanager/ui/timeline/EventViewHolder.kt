package ru.iandreyshev.timemanager.ui.timeline

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_event.view.*
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
        when (viewState.selection) {
            EventSelectionViewState.Normal ->
                itemView.time.text = viewState.endTime
            EventSelectionViewState.SelectedByTimer ->
                itemView.time.text = "${viewState.endTime} (selected)"
        }.exhaustive
    }

}

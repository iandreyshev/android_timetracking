package ru.iandreyshev.timemanager.ui.timeline

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_event.view.*

class EventViewHolder(
    view: View,
    onClickListener: (EventViewHolder) -> Unit
) : RecyclerView.ViewHolder(view) {

    init {
        itemView.clickableArea.setOnClickListener { onClickListener(this) }
    }

    fun bind(viewState: EventViewState) {
        itemView.title.text = viewState.title
        itemView.time.text = viewState.time
    }

}

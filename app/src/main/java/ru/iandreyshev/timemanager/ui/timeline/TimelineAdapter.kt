package ru.iandreyshev.timemanager.ui.timeline

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_timeline_event.view.*
import ru.iandreyshev.timemanager.R

class TimelineAdapter : RecyclerView.Adapter<EventViewHolder>() {

    var events: List<EventViewState> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    var onClickListener: (Int) -> Unit = {}
    var onLongClickListener: (Int) -> Unit = {}
    var onOptionsClick: (View, Int) -> Unit = { _, _ -> }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_timeline_event, parent, false)

        return EventViewHolder(
            view = view,
            onClickListener = { onClickListener(it.adapterPosition) },
            onLongClickListener = {
                onLongClickListener(it.adapterPosition)
                return@EventViewHolder true
            },
            onOptionsClick = {
                onOptionsClick(it.itemView.optionsButton, it.adapterPosition)
            }
        )
    }

    override fun getItemCount(): Int = events.count()

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
        holder.bind(events[position])

}

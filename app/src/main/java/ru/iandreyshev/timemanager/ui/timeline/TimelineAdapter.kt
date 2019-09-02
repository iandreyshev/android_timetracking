package ru.iandreyshev.timemanager.ui.timeline

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.iandreyshev.timemanager.R

class TimelineAdapter(
    private val onClickListener: (Int) -> Unit
) : RecyclerView.Adapter<EventViewHolder>() {

    var events: List<EventViewState> = listOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_event, parent, false)

        return EventViewHolder(view) {
            onClickListener(it.adapterPosition)
        }
    }

    override fun getItemCount(): Int = events.count()

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) =
        holder.bind(events[position])

}

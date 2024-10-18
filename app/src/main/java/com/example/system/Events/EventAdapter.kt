package com.example.system.Events

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.system.R

class EventsAdapter(
    private var eventsList: MutableList<Event>,
    private val onItemClick: (Event) -> Unit,
    private val onCheckboxChanged: (Event, Boolean) -> Unit
) : RecyclerView.Adapter<EventsAdapter.EventsViewHolder>() {

    class EventsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val checkbox: CheckBox = itemView.findViewById(R.id.checkbox)
        val textViewComingEvents: TextView = itemView.findViewById(R.id.TextViewComingEvents)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_event, parent, false)
        return EventsViewHolder(view)
    }

    override fun onBindViewHolder(holder: EventsViewHolder, position: Int) {
        val event = eventsList[position]

        holder.textViewComingEvents.text = event.name

        Log.d("EventsAdapter", "Binding event: ${event.name}, isCompleted: ${event.isCompleted}")
        holder.checkbox.setOnCheckedChangeListener(null)

        holder.checkbox.isChecked = event.isCompleted

        holder.itemView.setOnClickListener {
            onItemClick(event)
        }

        holder.checkbox.setOnCheckedChangeListener { _, isChecked ->
            Log.d("EventsAdapter", "Checkbox state changed for ${event.name}, isChecked: $isChecked")
            event.isCompleted = isChecked
            onCheckboxChanged(event, isChecked)
        }
    }

    override fun getItemCount(): Int {
        return eventsList.size
    }

    fun removeEvents(eventsToRemove: List<Event>) {
        eventsList.removeAll(eventsToRemove)
        notifyDataSetChanged()
    }

    fun setEvents(newEvents: List<Event>) {
        val diffResult = DiffUtil.calculateDiff(EventDiffCallback(eventsList, newEvents))
        eventsList.clear()
        eventsList.addAll(newEvents)
        diffResult.dispatchUpdatesTo(this)
    }
}

class EventDiffCallback(
    private val oldList: List<Event>,
    private val newList: List<Event>
) : DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}

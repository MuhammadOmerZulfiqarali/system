package com.example.system.Tasks

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.system.R

class TasksAdapter(private val onMarkAsDoneClick: (Int) -> Unit) : RecyclerView.Adapter<TasksAdapter.TaskViewHolder>() {
    private var tasks = listOf<Task>()

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.name_text_view)
        val markAsDoneTextView: TextView = itemView.findViewById(R.id.mark_as_done_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = tasks[position]
        holder.nameTextView.text = currentTask.taskName
        holder.markAsDoneTextView.setOnClickListener {
            onMarkAsDoneClick(currentTask.id) // Pass the task ID
        }
    }

    override fun getItemCount(): Int = tasks.size

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(newTasks: List<Task>) {
        Log.d("TasksAdapter", "Setting tasks: $newTasks")
        tasks = newTasks
        notifyDataSetChanged()
    }
}
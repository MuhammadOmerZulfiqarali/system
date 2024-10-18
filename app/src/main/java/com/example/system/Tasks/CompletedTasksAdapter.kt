package com.example.system.Tasks

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.system.R

class CompletedTasksAdapter(
    private val editClickListener: (Int) -> Unit,
    private val deleteClickListener: (Int) -> Unit
) : RecyclerView.Adapter<CompletedTasksAdapter.ViewHolder>() {

    private var tasks = listOf<Task>()

    @SuppressLint("NotifyDataSetChanged")
    fun setTasks(tasks: List<Task>) {
        this.tasks = tasks
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_completed_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = tasks[position]
        holder.bind(task)

        holder.itemView.findViewById<ImageButton>(R.id.btnEditCompletedTask).setOnClickListener {
            editClickListener(task.id) // Trigger the edit click listener
        }

        holder.itemView.findViewById<ImageButton>(R.id.btnDeleteCompletedTask).setOnClickListener {
            deleteClickListener(task.id) // Trigger the delete click listener
        }
    }

    override fun getItemCount(): Int = tasks.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val taskNameTextView: TextView = itemView.findViewById(R.id.tvTaskName)

        fun bind(task: Task) {
            taskNameTextView.text = task.taskName
        }
    }
}

package com.example.system.Tasks

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.system.R

class CompletedTasksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CompletedTasksAdapter
    private val taskViewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_completed_tasks, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewCompletedTasks)
        adapter = CompletedTasksAdapter(
            editClickListener = { taskId -> onEditTaskClick(taskId) },
            deleteClickListener = { taskId -> onDeleteTaskClick(taskId) }
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        taskViewModel.completedTasks.observe(viewLifecycleOwner) { tasks ->
            tasks?.let { adapter.setTasks(it) }
        }

        return view
    }

    private fun onEditTaskClick(taskId: Int) {
        taskViewModel.getTaskById(taskId).observe(viewLifecycleOwner) { task ->
            task?.let {
                showEditTaskDialog(it)
            }
        }
    }

    private fun onDeleteTaskClick(taskId: Int) {
        showDeleteConfirmationDialog(taskId)
    }

    private fun showEditTaskDialog(task: Task) {
        val taskNameEditText = EditText(requireContext()).apply {
            setText(task.taskName)
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Update task name")
            .setView(taskNameEditText)
            .setPositiveButton("Update") { _, _ ->
                val updatedTask = task.copy(taskName = taskNameEditText.text.toString())
                taskViewModel.update(updatedTask)  // Update the task in the ViewModel
            }
            .setNegativeButton("Cancel", null)
            .create()
            .show()
    }

    private fun showDeleteConfirmationDialog(taskId: Int) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                taskViewModel.deleteTask(taskId) // Delete the task in the ViewModel
            }
            .setNegativeButton("No", null)
            .show()
    }
}

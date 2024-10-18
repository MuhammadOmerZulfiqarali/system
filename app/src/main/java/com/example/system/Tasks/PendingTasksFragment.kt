package com.example.system.Tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.system.R

class PendingTasksFragment : Fragment() {

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var adapter: TasksAdapter
    private val taskViewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pending_tasks, container, false)

        taskRecyclerView = view.findViewById(R.id.recyclerViewPendingTasks)
        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val database = TaskDatabase.getDatabase(requireContext())
        val taskDao = database.taskDao()
        val repository = TaskRepository(taskDao)


        adapter = TasksAdapter { taskId -> onMarkAsDoneClick(taskId) }
        taskRecyclerView.adapter = adapter

        taskViewModel.allPendingTasks.observe(viewLifecycleOwner) { tasks ->
            Log.d("PendingTasksFragment", "Received tasks: $tasks")
            tasks?.let { adapter.setTasks(it) }
        }

        return view
    }

    private fun onMarkAsDoneClick(taskId: Int) {
        taskViewModel.markTaskAsDone(taskId)
    }
}

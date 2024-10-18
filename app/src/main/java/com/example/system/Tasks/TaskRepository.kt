package com.example.system.Tasks

import androidx.lifecycle.LiveData

class TaskRepository(private val taskDao: TaskDao) {
    val pendingTasks: LiveData<List<Task>> = taskDao.getPendingTasks()
    val completedTasks: LiveData<List<Task>> = taskDao.getCompletedTasks()

    fun getTaskById(taskId: Int): Task? {
        return taskDao.getTaskById(taskId)
    }

    fun insert(task: Task) {
        taskDao.insert(task)
    }

    fun update(task: Task) {
        taskDao.update(task)
    }

    fun delete(taskId: Int) {
        taskDao.deleteTaskById(taskId)
    }
}

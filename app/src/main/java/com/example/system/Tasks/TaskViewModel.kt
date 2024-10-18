package com.example.system.Tasks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val taskDao: TaskDao = TaskDatabase.getDatabase(application).taskDao()
    private val repository: TaskRepository = TaskRepository(taskDao)

    // LiveData for pending and completed tasks
    val allPendingTasks: LiveData<List<Task>> = repository.pendingTasks
    val completedTasks: LiveData<List<Task>> = repository.completedTasks

    // LiveData for operation results
    private val _operationResult = MutableLiveData<Result<Unit>>()
    val operationResult: LiveData<Result<Unit>> get() = _operationResult

    // Insert a new task
    fun insert(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.insert(task)
            _operationResult.postValue(Result.success(Unit))
        } catch (e: Exception) {
            _operationResult.postValue(Result.failure(e))
        }
    }

    // Update an existing task
    fun update(task: Task) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.update(task)
            _operationResult.postValue(Result.success(Unit))
        } catch (e: Exception) {
            _operationResult.postValue(Result.failure(e))
        }
    }

    // Mark a task as done
    fun markTaskAsDone(taskId: Int) = viewModelScope.launch(Dispatchers.IO) {
        try {
            taskDao.markTaskAsDone(taskId)
            _operationResult.postValue(Result.success(Unit))
        } catch (e: Exception) {
            _operationResult.postValue(Result.failure(e))
        }
    }

    // Delete an existing task
    fun deleteTask(taskId: Int) = viewModelScope.launch(Dispatchers.IO) {
        try {
            repository.delete(taskId) // Call the repository method to delete the task
            _operationResult.postValue(Result.success(Unit)) // Notify success
        } catch (e: Exception) {
            _operationResult.postValue(Result.failure(e)) // Notify failure
        }
    }

    // Get a task by ID
    fun getTaskById(taskId: Int): LiveData<Task?> {
        val taskLiveData = MutableLiveData<Task?>()
        viewModelScope.launch(Dispatchers.IO) {
            val task = repository.getTaskById(taskId) // Fetch task from the repository
            taskLiveData.postValue(task)
        }
        return taskLiveData
    }

}

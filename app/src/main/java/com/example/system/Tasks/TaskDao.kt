package com.example.system.Tasks

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks_table WHERE status = 0") // Assuming 0 is for pending tasks
    fun getPendingTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks_table WHERE status = 1") // Assuming 1 is for completed tasks
    fun getCompletedTasks(): LiveData<List<Task>>

    @Query("SELECT * FROM tasks_table WHERE id = :taskId LIMIT 1")
    fun getTaskById(taskId: Int): Task?

    @Insert
    fun insert(task: Task)

    @Update
    fun update(task: Task)

    @Query("DELETE FROM tasks_table WHERE id = :taskId")
    fun deleteTaskById(taskId: Int)

    @Query("UPDATE tasks_table SET status = 1 WHERE id = :taskId")
    fun markTaskAsDone(taskId: Int)
}

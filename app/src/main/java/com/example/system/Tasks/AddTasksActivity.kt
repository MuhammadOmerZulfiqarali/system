package com.example.system.Tasks

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.system.MainActivity
import com.example.system.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddTasksActivity : AppCompatActivity() {

    private lateinit var taskNameInput: EditText
    private lateinit var locationInput: EditText
    private lateinit var addButton: Button
    private lateinit var homeImageButton: ImageButton
    private lateinit var taskDatabase: TaskDatabase
    private lateinit var taskRepository: TaskRepository

    private var isAllFieldsChecked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_tasks)

        homeImageButton = findViewById(R.id.btnAddTasks)
        taskNameInput = findViewById(R.id.task_name_input)
        locationInput = findViewById(R.id.location_input)
        addButton = findViewById(R.id.add_button)



        homeImageButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        taskDatabase = TaskDatabase.getDatabase(this)
        taskRepository = TaskRepository(taskDatabase.taskDao())

        addButton.setOnClickListener {
            val taskName = taskNameInput.text.toString().trim()
            val location = locationInput.text.toString().trim()

            if (taskName.isNotEmpty() && location.isNotEmpty()) {
                addTask(taskName, location)
            } else {
                Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            }
            isAllFieldsChecked = CheckAllFields()
        }
    }

    private fun CheckAllFields(): Boolean {
        if (taskNameInput.length() == 0) {
            taskNameInput.error = "This Field is Required"
            return false
        }
        if (locationInput.length() == 0){
            locationInput.error = "Location is  Required"
            return false

        }
        return true
    }



    private fun addTask(taskName: String, location: String) {
        val task = Task(taskName = taskName, location = location)

        // Insert the task into the database using coroutines
        lifecycleScope.launch(Dispatchers.IO) {
            taskRepository.insert(task)
            runOnUiThread {
                Toast.makeText(this@AddTasksActivity, "Task added", Toast.LENGTH_SHORT).show()
                finish()  // Close the activity after adding the task
            }
        }
    }
}

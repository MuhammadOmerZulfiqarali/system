package com.example.system.Tasks

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.system.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class TasksActivity : AppCompatActivity() {

    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var homeImageButton: ImageButton
    private val taskViewModel: TaskViewModel by viewModels()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("TasksActivity", "Before setContentView")
        setContentView(R.layout.activity_tasks)
        Log.d("TasksActivity", "After setContentView")

        tabLayout = findViewById(R.id.tabLayoutTasks)
        viewPager2 = findViewById(R.id.viewPagerTasks)

        // Set up ViewPager2 with TasksPagerAdapter
        val tasksPagerAdapter = TasksPagerAdapter(this)
        viewPager2.adapter = tasksPagerAdapter

        // Attach TabLayout with ViewPager2 using TabLayoutMediator
        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when (position) {
                0 -> "PENDING TASKS"
                1 -> "COMPLETED TASKS"
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }.attach()

        // Home button click listener
        homeImageButton = findViewById(R.id.btnHomeTasks)
        homeImageButton.setOnClickListener {
            finish() // Navigate back to home or previous activity
        }

        // Floating action button to add a new task
        val fab: FloatingActionButton = findViewById(R.id.floatingButton)
        fab.setOnClickListener {
            startActivity(Intent(this, AddTasksActivity::class.java))
        }
    }
}

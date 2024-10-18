package com.example.system.Events

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.system.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import androidx.viewpager2.widget.ViewPager2
import com.example.system.MainActivity
import com.google.android.material.tabs.TabLayoutMediator

class EventsActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager: ViewPager2
    private lateinit var fabEventButton: FloatingActionButton
    private lateinit var homeButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var eventsAdapter: EventsAdapter
    private var selectedEvents: MutableList<Event> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events)

        tabLayout = findViewById(R.id.tabLayoutEvents)
        viewPager = findViewById(R.id.ViewPagerEvents)
        fabEventButton = findViewById(R.id.FabEventButton)
        homeButton = findViewById(R.id.homeButton)
        deleteButton = findViewById(R.id.deleteButtonEvents)

        deleteButton.visibility = View.GONE

        if (!::eventsAdapter.isInitialized) {
            eventsAdapter = EventsAdapter(mutableListOf(), onItemClick = { event ->

                val intent = Intent(this, EventDetailsActivity::class.java)
                intent.putExtra("EVENT_ID", event.id)
                startActivity(intent)
            }, onCheckboxChanged = { event, isChecked ->
                if (isChecked) {
                    selectedEvents.add(event)
                } else {
                    selectedEvents.remove(event)
                }
                updateDeleteButtonVisibility()
            })
        }

        viewPager.adapter = EventsPagerAdapter(this, eventsAdapter)

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "COMING EVENTS"
                1 -> "PAST EVENTS"
                else -> throw IllegalStateException("Unexpected position $position")
            }
        }.attach()

        fabEventButton.setOnClickListener {
            val intent = Intent(this, AddEventActivity::class.java)
            startActivity(intent)
        }

        homeButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }
    }

    private fun updateDeleteButtonVisibility() {
        deleteButton.visibility = if (selectedEvents.isNotEmpty())
            View.VISIBLE
        else
            View.GONE
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Confirmation")
            .setMessage("Are you sure you want to delete the selected events?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteSelectedEvents()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun deleteSelectedEvents() {
        eventsAdapter.removeEvents(selectedEvents)
        selectedEvents.clear()
        updateDeleteButtonVisibility()
    }
}

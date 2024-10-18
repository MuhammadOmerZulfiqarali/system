package com.example.system.Events

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.system.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class PastEventsFragment : Fragment() {

    private lateinit var viewModel: EventViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter
    private var selectedEvents: MutableList<Event> = mutableListOf()
    private lateinit var btnDelete: ImageButton

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_past_events, container, false)

        // Initialize views from the inflated layout
        recyclerView = view.findViewById(R.id.RecyclerViewPastEvents)
        btnDelete = requireActivity().findViewById(R.id.deleteButtonEvents)

        recyclerView.layoutManager = LinearLayoutManager(context)

        // Initialize the adapter for the RecyclerView
        eventsAdapter = EventsAdapter(mutableListOf(), { event ->
            val intent = Intent(requireContext(), EventDetailsActivity::class.java)
            intent.putExtra("EVENT_ID", event.id)
            startActivity(intent)
        }, { event, isChecked ->
            if (isChecked) {
                selectedEvents.add(event)
            } else {
                selectedEvents.remove(event)
            }
            updateDeleteButtonVisibility()
        })
        recyclerView.adapter = eventsAdapter

        // Set up the ViewModel
        val database = EventsDatabase.getDatabase(requireContext())
        val eventDao = database.eventDao()
        val repository = EventRepository(eventDao)
        val factory = EventViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        // Set initial visibility for the delete button
        updateDeleteButtonVisibility()

        // Observe the events from the ViewModel
        viewModel.allEvents.observe(viewLifecycleOwner) { events ->
            Log.d("PastEventsFragment", "Events received: ${events.size} events")
            if (events.isNullOrEmpty()) {
                Toast.makeText(context, "No events found", Toast.LENGTH_SHORT).show()
            } else {
                val pastEvents = events.filter { event -> isPastEvent(event) }
                Log.d("PastEventsFragment", "Past events found: ${pastEvents.size}")

                if (pastEvents.isEmpty()) {
                    Toast.makeText(context, "No past events available", Toast.LENGTH_SHORT).show()
                } else {
                    eventsAdapter.setEvents(pastEvents)
                }
            }
        }

        // Handle delete button click
        btnDelete.setOnClickListener {
            if (selectedEvents.isNotEmpty()) {
                showDeleteConfirmationDialog()
            }
        }

        return view
    }

    // Show a confirmation dialog before deleting events
    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete")
            .setMessage("Are you sure you want to delete the selected events?")
            .setPositiveButton("Yes") { dialog, _ ->
                deleteSelectedEvents() // Proceed with deletion
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog
            }
            .create()
            .show()
    }

    // Delete selected events from the ViewModel and adapter
    private fun deleteSelectedEvents() {
        selectedEvents.forEach{ event ->
            viewModel.deleteEventById(event.id)

        }
        viewModel.deleteEvents(selectedEvents)
        eventsAdapter.removeEvents(selectedEvents)
        selectedEvents.clear()
        updateDeleteButtonVisibility()
    }

    // Update the visibility of the delete button based on selected events
    private fun updateDeleteButtonVisibility() {
        btnDelete.visibility = if (selectedEvents.isNotEmpty()) {
            View.VISIBLE // Show delete button
        } else {
            View.GONE // Hide delete button
        }
    }

    // Check if an event is a past event
    @RequiresApi(Build.VERSION_CODES.O)
    private fun isPastEvent(event: Event): Boolean {
        // Assuming event.date is in "dd-MM-yyyy" format and event.time is in "HH:mm" format
        val dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        return try {
            // Parse the date and time separately
            val eventDate = LocalDate.parse(event.date, dateFormatter)
            val eventTime = LocalTime.parse(event.time, timeFormatter)

            // Combine date and time to create a LocalDateTime
            val eventDateTime = LocalDateTime.of(eventDate, eventTime)

            // Check if the event time is before the current time
            val isPast = eventDateTime.isBefore(LocalDateTime.now())

            Log.d("PastEventsFragment", "Event: ${event.name}, EventTime: $eventDateTime, IsPast: $isPast")
            isPast
        } catch (e: DateTimeParseException) {
            Log.e("PastEventsFragment", "Error parsing date: ${event.date} ${event.time}", e)
            false
        }
    }

}

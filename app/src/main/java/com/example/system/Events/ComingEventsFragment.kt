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
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.system.R
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ComingEventsFragment : Fragment() {

    private lateinit var viewModel: EventViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var eventsAdapter: EventsAdapter
    private var selectedEvents: MutableList<Event> = mutableListOf()
    private lateinit var deleteButton: ImageButton

    @SuppressLint("MissingInflatedId")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_coming_events, container, false)

        deleteButton = requireActivity().findViewById(R.id.deleteButtonEvents)

        recyclerView = view.findViewById(R.id.RecyclerViewComingEvents)

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
        recyclerView.layoutManager = LinearLayoutManager(context)

        val database = EventsDatabase.getDatabase(requireContext())
        val eventDao = database.eventDao()
        val repository = EventRepository(eventDao)
        val factory = EventViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[EventViewModel::class.java]

        viewModel.allEvents.observe(viewLifecycleOwner) { events ->
            val comingEvents = events.filter { event -> isUpcomingEvent(event) }
            eventsAdapter.setEvents(comingEvents)
        }

        // Set up the delete button click listener
        deleteButton.setOnClickListener {
            if (selectedEvents.isNotEmpty()) {
                showDeleteConfirmationDialog()
            }
        }

        return view
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete")
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
        selectedEvents.forEach { event ->
            viewModel.deleteEventById(event.id)
        }
        viewModel.deleteEvents(selectedEvents)

        eventsAdapter.removeEvents(selectedEvents)
        selectedEvents.clear()
        updateDeleteButtonVisibility()
    }

    private fun updateDeleteButtonVisibility() {
        deleteButton.visibility = if (selectedEvents.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun isUpcomingEvent(event: Event): Boolean {
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm")
        val dateTimeString = "${event.date} ${event.time}"

        return try {
            val eventTime = LocalDateTime.parse(dateTimeString, formatter)
            eventTime.isAfter(LocalDateTime.now())
        } catch (e: Exception) {
            false
        }
    }
}

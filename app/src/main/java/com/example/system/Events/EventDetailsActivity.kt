package com.example.system.Events

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.system.MainActivity
import com.example.system.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var eventViewModel: EventViewModel
    private lateinit var eventName: TextView
    private lateinit var eventLocation: TextView
    private lateinit var eventDate: TextView
    private lateinit var eventTime: TextView
    private lateinit var btnEditEventName: ImageButton
    private lateinit var btnEditEventLocation: ImageButton
    private lateinit var btnEditEventDate: ImageButton
    private lateinit var btnEditEventTime: ImageButton
    private lateinit var btnHome: ImageButton

    private var eventId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        eventName = findViewById(R.id.eventNameLabel)
        eventLocation = findViewById(R.id.eventLocationLabel)
        eventDate = findViewById(R.id.eventDateLabel)
        eventTime = findViewById(R.id.eventTimeLabel)
        btnEditEventName = findViewById(R.id.editEventName)
        btnEditEventLocation = findViewById(R.id.editEventLocation)
        btnEditEventDate = findViewById(R.id.editEventDate)
        btnEditEventTime = findViewById(R.id.editEventTime)
        btnHome = findViewById(R.id.homeButtonEventDetails)

        eventId = intent.getIntExtra("EVENT_ID", -1)

        val eventDao = EventsDatabase.getDatabase(application).eventDao()
        val repository = EventRepository(eventDao)
        val viewModelFactory = EventViewModelFactory(repository)
        eventViewModel = ViewModelProvider(this, viewModelFactory).get(EventViewModel::class.java)

        loadEventData(eventId)

        btnEditEventName.setOnClickListener {
            showEditDialog("Event Name", eventName.text.toString()) }

        btnEditEventLocation.setOnClickListener {
            showEditDialog("Event Location", eventLocation.text.toString()) }

        btnEditEventDate.setOnClickListener {
            showEditDialog("Event Date", eventDate.text.toString()) }

        btnEditEventTime.setOnClickListener {
            showEditDialog("Event Time", eventTime.text.toString()) }

        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) }
    }

    private fun loadEventData(id: Int) {
        eventViewModel.allEvents.observe(this) { events ->
            val event = events.find { it.id == id }
            if (event != null) {
                eventName.text = event.name
                eventLocation.text = event.location
                eventDate.text = event.date
                eventTime.text = event.time
            } else {
                Toast.makeText(this, "Event not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEditDialog(fieldName: String, currentValue: String) {
        val builder = AlertDialog.Builder(this)
        val input = android.widget.EditText(this)
        input.setText(currentValue)

        builder.setTitle("Edit $fieldName")
        builder.setView(input)
        builder.setPositiveButton("Update") { _, _ ->
            val newValue = input.text.toString()
            if (newValue.isBlank()) {
                Toast.makeText(this, "$fieldName cannot be empty", Toast.LENGTH_SHORT).show()
                return@setPositiveButton
            }

            when (fieldName) {
                "Event Name" -> eventName.text = newValue
                "Event Location" -> eventLocation.text = newValue
                "Event Date" -> eventDate.text = newValue
                "Event Time" -> eventTime.text = newValue
            }

            updateEventData(newValue, fieldName)
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun updateEventData(newValue: String, fieldName: String) {
        val updatedEvent = Event(
            id = eventId,
            name = if (fieldName == "Event Name") newValue
            else eventName.text.toString(),
            location = if (fieldName == "Event Location") newValue
            else eventLocation.text.toString(),
            date = if (fieldName == "Event Date") newValue
            else eventDate.text.toString(),
            time = if (fieldName == "Event Time") newValue
            else eventTime.text.toString()
        )

        CoroutineScope(Dispatchers.IO).launch {
            val db = EventsDatabase.getDatabase(applicationContext)
            db.eventDao().update(updatedEvent)

            runOnUiThread {
                Toast.makeText(this@EventDetailsActivity, "Event Updated Successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
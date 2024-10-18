@file:Suppress("DEPRECATION")

package com.example.system.Events

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.system.MainActivity
import com.example.system.R
import kotlinx.coroutines.launch
import java.util.*

class AddEventActivity : AppCompatActivity() {

    private lateinit var eventNameEditText: EditText
    private lateinit var eventLocationEditText: EditText
    private lateinit var timePicker: TimePicker
    private lateinit var addEventButton: Button
    private lateinit var selectedDateTextView: TextView
    private lateinit var btnHome: ImageButton
    private lateinit var eventDatabase: EventsDatabase
    private var selectedDate: String? = null

    var isAllFieldsChecked = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_events)

        eventNameEditText = findViewById(R.id.etEventName)
        eventLocationEditText = findViewById(R.id.etEventLocation)
        timePicker = findViewById(R.id.timePicker)
        addEventButton = findViewById(R.id.AddEvent)
        selectedDateTextView = findViewById(R.id.tvSelectDate)
        btnHome = findViewById(R.id.btnHomeEvents)


        eventDatabase = EventsDatabase.getDatabase(this)

        selectedDateTextView.setOnClickListener {
            showDatePickerDialog()
        }
        btnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        addEventButton.setOnClickListener {
            addEventToDatabase()

            isAllFieldsChecked = CheckAllFields()

        }
    }

    private fun CheckAllFields(): Boolean {
        if (eventNameEditText.length() == 0){
            eventNameEditText.error = "Event Name Required"
            return false
        }
        if (eventLocationEditText.length() == 0){
            eventLocationEditText.error = "Event Location Required"
            return false
        }
        if (selectedDateTextView.length() == 0){
            selectedDateTextView.error = "Select Date Required"
            return false
        }
        return true

    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDate = String.format("%02d-%02d-%04d", selectedDay, selectedMonth + 1, selectedYear)
                selectedDateTextView.text = selectedDate // Use text property instead of setText
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }

    private fun addEventToDatabase() {
        val eventName = eventNameEditText.text.toString().trim()
        val eventLocation = eventLocationEditText.text.toString().trim()

        if (eventName.isEmpty() || eventLocation.isEmpty() || selectedDate.isNullOrEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val hour: Int
        val minute: Int

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hour = timePicker.hour
            minute = timePicker.minute
        } else {
            hour = timePicker.currentHour // Use currentHour for older versions
            minute = timePicker.currentMinute // Use currentMinute for older versions
        }

        val time = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)

        val event = Event(
            name = eventName,
            location = eventLocation,
            date = selectedDate!!,
            time = time
        )

        lifecycleScope.launch {
            eventDatabase.eventDao().insert(event)
            Log.d("AddEventActivity", "Event added: $event")
            Toast.makeText(this@AddEventActivity, "Event added", Toast.LENGTH_SHORT).show()
            finish() // Close the activity after adding the event
        }
    }
}

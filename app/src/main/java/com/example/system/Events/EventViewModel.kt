package com.example.system.Events

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class EventViewModel(private val repository: EventRepository) : ViewModel() {

    // LiveData for all events
    val allEvents: LiveData<List<Event>> = repository.getAllEvents

    // Insert a new event
    fun insert(event: Event) = viewModelScope.launch {
        repository.insert(event)
    }

    // Delete an event by id
    fun deleteEventById(eventId: Int) = viewModelScope.launch {
        Log.d("EventViewModel", "Deleting event with ID: $eventId")
        repository.deleteEventById(eventId)
    }


    // Update an event
    fun update(event: Event) = viewModelScope.launch {
        repository.update(event)
    }
    fun deleteEvents(events: List<Event>) = viewModelScope.launch {
        repository.deleteEvents(events)
    }
}

package com.example.system.Events

import android.util.Log
import androidx.lifecycle.LiveData

class EventRepository(private val eventDao: EventDao) {

    val getAllEvents: LiveData<List<Event>> = eventDao.getAllEvents()

    suspend fun insert(event: Event) {
        eventDao.insert(event)
    }

    suspend fun deleteEventById(eventId: Int) {
        Log.d("EventRepository", "Calling DAO to delete event with ID: $eventId")
        eventDao.deleteEventById(eventId)
    }

    suspend fun update(event: Event) {
        eventDao.update(event)
    }
    suspend fun deleteEvents(events: List<Event>) {
        eventDao.deleteEvents(events)
    }
}

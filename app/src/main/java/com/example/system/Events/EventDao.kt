package com.example.system.Events

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: Event)

    @Query("SELECT * FROM events_table ORDER BY date ASC")
    fun getAllEvents(): LiveData<List<Event>>

    @Query("DELETE FROM events_table WHERE id = :eventId")
    suspend fun deleteEventById(eventId: Int)

    @Update
    suspend fun update(event: Event)

    @Delete
    suspend fun deleteEvents(events: List<Event>)

}

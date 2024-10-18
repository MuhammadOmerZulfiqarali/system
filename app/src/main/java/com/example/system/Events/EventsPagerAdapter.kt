package com.example.system.Events

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class EventsPagerAdapter(
    fragmentActivity: FragmentActivity,
    private val eventsAdapter: EventsAdapter // Pass the EventsAdapter
) : FragmentStateAdapter(fragmentActivity) {

    override fun getItemCount(): Int {
        return 2 // Number of tabs (Upcoming Events, Past Events)
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ComingEventsFragment() // Pass the adapter to ComingEventsFragment
            1 -> PastEventsFragment() // Pass the adapter to PastEventsFragment
            else -> throw IllegalStateException("Unexpected position $position")
        }
    }
}

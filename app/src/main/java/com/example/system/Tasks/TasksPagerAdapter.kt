package com.example.system.Tasks

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelStoreOwner
import androidx.viewpager2.adapter.FragmentStateAdapter

class TasksPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {

    override fun getItemCount(): Int {
        return 2 // Number of tabs
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PendingTasksFragment()  // Replace with your actual fragment
            1 -> CompletedTasksFragment()  // Replace with your actual fragment
            else -> PendingTasksFragment()
        }
    }
}

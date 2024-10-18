package com.example.system.Friends


import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class FriendViewModel(private val repository: FriendRepository) : ViewModel() {

    // Expose LiveData for the list of friends
    val allFriends: LiveData<List<Friend>> = repository.getAllFriends()

    // Function to insert a friend
    fun insert(friend: Friend) {
        viewModelScope.launch {
            repository.insert(friend)
        }
    }
}

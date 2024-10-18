package com.example.system.Friends

import androidx.lifecycle.LiveData

class FriendRepository(private val friendDao: FriendDao) {

    // Get all friends as LiveData
    fun getAllFriends(): LiveData<List<Friend>> {
        return friendDao.getAllFriends()
    }

    // Insert a friend
    suspend fun insert(friend: Friend) {
        friendDao.insertFriend(friend)
    }
}




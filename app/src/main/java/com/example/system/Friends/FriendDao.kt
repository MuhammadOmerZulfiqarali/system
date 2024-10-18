package com.example.system.Friends

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface FriendDao {
    @Insert
    suspend fun insertFriend(friend: Friend)

    @Update
    suspend infix fun updateFriend(friend: Friend)

    @Query("SELECT * FROM friend_table")
    fun getAllFriends(): LiveData<List<Friend>>

    @Query("SELECT * FROM friend_table WHERE id = :friendId LIMIT 1")
    suspend fun getFriendById(friendId: Int): Friend?

    @Query("DELETE FROM friend_table WHERE id = :friendId")
    suspend fun deleteFriend(friendId: Int)
}
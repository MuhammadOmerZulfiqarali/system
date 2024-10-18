package com.example.system.Friends

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_table")
data class Friend(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val firstName: String,
    val lastName: String,
    val age: Int,
    val address: String,
    val gender: String, // Add this if you have a new column
    val imageUri: String? // Add this line

)

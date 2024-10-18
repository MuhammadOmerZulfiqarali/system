package com.example.system.Friends

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.system.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FriendsActivity : AppCompatActivity() {

    private lateinit var friendViewModel: FriendViewModel
    private lateinit var homeImageButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_friends)

        homeImageButton = findViewById(R.id.rBtnHome)

        findViewById<ImageButton>(R.id.rBtnHome).setOnClickListener { finish() }



        // Initialize RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView) // Make sure to have this in your layout
        val adapter = FriendAdapter(this)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the database and repository
        val friendDao = FriendDatabase.getDatabase(application).friendDao()
        val repository = FriendRepository(friendDao)

        // Initialize ViewModel
        friendViewModel = FriendViewModel(repository)

        // Observe the LiveData from ViewModel
        friendViewModel.allFriends.observe(this, Observer { friends ->
            friends?.let { adapter.submitList(it) }
        })


        // FloatingActionButton to add a friend
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this, AddFriendActivity::class.java)
            startActivity(intent)
        }
    }
}

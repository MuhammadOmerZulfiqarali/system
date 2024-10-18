@file:Suppress("DEPRECATION")

package com.example.system.Friends

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.example.system.MainActivity
import com.example.system.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateFriendActivity : AppCompatActivity() {

    private lateinit var friendViewModel: FriendViewModel
    private lateinit var imageViewUpdateFriend: ImageView
    private lateinit var txtFirstName: TextView
    private lateinit var txtLastName: TextView
    private lateinit var txtAge: TextView
    private lateinit var txtAddress: TextView
    private lateinit var updateGender: TextView
    private lateinit var btnEditImage: Button
    private lateinit var btnDelete: Button
    private lateinit var btnHomeImage: ImageButton

    private var imageUri: Uri? = null
    private var friendId: Int = 0

    companion object {
        private const val PICK_IMAGE = 1
        private const val STORAGE_PERMISSION_CODE = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_friend)

        // Initialize views
        imageViewUpdateFriend = findViewById(R.id.imageViewUpdateFriend)
        txtFirstName = findViewById(R.id.txtFirstName)
        txtLastName = findViewById(R.id.txtLastName)
        txtAge = findViewById(R.id.txtAge)
        txtAddress = findViewById(R.id.txtAddress)
        updateGender = findViewById(R.id.updateGender)
        btnEditImage = findViewById(R.id.btnEditImage)
        btnDelete = findViewById(R.id.btnDelete)
        btnHomeImage = findViewById(R.id.homeBtnUpdateFriend)


        // Get the friend ID from the Intent
        friendId = intent.getIntExtra("FRIEND_ID", -1)

        // Initialize ViewModel
        val friendDao = FriendDatabase.getDatabase(application).friendDao()
        val repository = FriendRepository(friendDao)
        friendViewModel = ViewModelProvider(this, ViewModelFactory(repository)).get(FriendViewModel::class.java)

        // Load existing friend data
        loadFriendData(friendId)

        // Request storage permission
        requestStoragePermission()

        // Set up button listeners
        btnEditImage.setOnClickListener {
            openGallery() }

        findViewById<ImageButton>(R.id.updateBtnEditFirstName).setOnClickListener {
            showEditDialog("First Name", txtFirstName.text.toString()) }

        findViewById<ImageButton>(R.id.UpdateBtnEditLastName).setOnClickListener {
            showEditDialog("Last Name", txtLastName.text.toString()) }

        findViewById<ImageButton>(R.id.updateBtnEditAge).setOnClickListener {
            showEditDialog("Age", txtAge.text.toString()) }

        findViewById<ImageButton>(R.id.updateBtnEditAddress).setOnClickListener {
            showEditDialog("Address", txtAddress.text.toString()) }

        findViewById<ImageButton>(R.id.updateBtnEditGender).setOnClickListener {
            showEditDialog("Gender", updateGender.text.toString()) }

        btnDelete.setOnClickListener {
            confirmDeleteFriend() }

        findViewById<ImageButton>(R.id.homeBtnUpdateFriend).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) }

    }

    private fun loadFriendData(id: Int) {
        friendViewModel.allFriends.observe(this) { friends ->
            val friend = friends.find { it.id == id }
            if (friend != null) {
                txtFirstName.text = friend.firstName
                txtLastName.text = friend.lastName
                txtAge.text = friend.age.toString()
                txtAddress.text = friend.address
                updateGender.text = friend.gender

                friend.imageUri?.let { uriString ->
                    imageUri = Uri.parse(uriString)

                    if (isUriAccessible(imageUri)) {
                        imageViewUpdateFriend.setImageURI(imageUri)
                    } else {
                        Toast.makeText(this, "Cannot access the image", Toast.LENGTH_SHORT).show()
                        imageViewUpdateFriend.setImageResource(R.drawable.persons)
                    }
                } ?: run {
                    imageViewUpdateFriend.setImageResource(R.drawable.persons)
                }
            } else {
                Toast.makeText(this, "Friend not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun isUriAccessible(uri: Uri?): Boolean {
        return try {
            uri?.let {
                contentResolver.openInputStream(it)?.use { true } ?: false
            } ?: false
        } catch (e: Exception) {
            Log.e("UpdateFriendActivity", "URI access error: ${e.message}")
            false
        }
    }

    private fun showEditDialog(fieldName: String, currentValue: String) {
        val builder = AlertDialog.Builder(this)
        val input = android.widget.EditText(this)
        input.setText(currentValue)
        builder.setTitle("Edit $fieldName")
        builder.setView(input)
        builder.setPositiveButton("Update") { _, _ ->
            when (fieldName) {
                "First Name" -> txtFirstName.text = input.text
                "Last Name" -> txtLastName.text = input.text
                "Age" -> txtAge.text = input.text
                "Address" -> txtAddress.text = input.text
                "Gender" -> updateGender.text = input.text
            }
            updateFriendData()
        }
        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun updateFriendData() {
        // Create an updated friend object with the new imageUri
        val updatedFriend = Friend(
            id = friendId,
            firstName = txtFirstName.text.toString(),
            lastName = txtLastName.text.toString(),
            age = txtAge.text.toString().toInt(),
            address = txtAddress.text.toString(),
            gender = updateGender.text.toString(),
            imageUri = imageUri?.toString() // Ensure this is correctly capturing the new URI
        )

        Log.d("UpdateFriendActivity", "Updating friend with new image URI: $updatedFriend")

        CoroutineScope(Dispatchers.IO).launch {
            val db = FriendDatabase.getDatabase(applicationContext)

            // Update the friend in the database
            db.friendDao().updateFriend(updatedFriend)

            // Query the database to retrieve the updated friend
            val friend = db.friendDao().getFriendById(friendId)
            Log.d("UpdateFriendActivity", "Friend after update: $friend") // Log the friend to see the updated imageUri

            runOnUiThread {
                Toast.makeText(this@UpdateFriendActivity, "Friend updated successfully!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDeleteFriend() {
        AlertDialog.Builder(this).apply {
            setTitle("Delete Friend")
            setMessage("Are you sure you want to delete ${txtFirstName.text}?")
            setPositiveButton("Yes") { _, _ -> deleteFriend() }
            setNegativeButton("No", null)
            show()
        }
    }

    private fun deleteFriend() {
        CoroutineScope(Dispatchers.IO).launch {
            val db = FriendDatabase.getDatabase(applicationContext)
            db.friendDao().deleteFriend(friendId)
            runOnUiThread {
                Toast.makeText(this@UpdateFriendActivity, "Friend deleted successfully!", Toast.LENGTH_SHORT).show()
                finish() // Close activity
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data
            Log.d("UpdateFriendActivity", "New Image URI selected: $imageUri")

            if (isUriAccessible(imageUri)) {
                imageViewUpdateFriend.setImageURI(imageUri)
                updateFriendData() // Update the database with the new image URI
            } else {
                Toast.makeText(this, "Cannot access the selected image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                Toast.makeText(this, "Storage permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied to read storage", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
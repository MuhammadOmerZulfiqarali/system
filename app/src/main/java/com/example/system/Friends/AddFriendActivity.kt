@file:Suppress("DEPRECATION")

package com.example.system.Friends

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.system.MainActivity
import com.example.system.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddFriendActivity : AppCompatActivity() {

    private lateinit var imageViewAddFriend: ImageView
    private lateinit var firstName: EditText
    private lateinit var lastName: EditText
    private lateinit var age: EditText
    private lateinit var address: EditText
    private lateinit var genderGroup: RadioGroup
    private lateinit var addButton: Button
    private var imageUri: Uri? = null
    private lateinit var homeImageButton: ImageButton

    var isAllFieldsChecked = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_friend)

        // Initialize views
        imageViewAddFriend = findViewById(R.id.imageViewAddFriend)
        firstName = findViewById(R.id.firstName)
        lastName = findViewById(R.id.lastName)
        age = findViewById(R.id.Age)
        address = findViewById(R.id.address)
        genderGroup = findViewById(R.id.genderGroup)
        addButton = findViewById(R.id.addButton)
        homeImageButton = findViewById(R.id.imageBtnHomeFriend)

        findViewById<ImageButton>(R.id.imageBtnHomeFriend).setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent) }


        imageViewAddFriend.setOnClickListener {
            openGallery()
        }
        isAllFieldsChecked = CheckAllFields()


        // Handle Add Button click
        addButton.setOnClickListener {
            if (CheckAllFields()) {
                // Insert friend data only if validation passes
                insertFriendData()
            }
        }
    }

    private fun CheckAllFields(): Boolean {

        var isValid= true

        if (firstName.text.isNullOrEmpty()) {
            firstName.error = "First Name Required"
            isValid = false
        }
        if (lastName.text.isNullOrEmpty()){
            lastName.error = "Last Name Required"
            isValid = false
        }
        if (age.text.isNullOrEmpty()) {
            age.error = "Age Required"
            isValid = false
        }
        if (address.text.isNullOrEmpty()){
            address.error = "Address Required"
            isValid = false
        }
        if (imageUri == null){
            Toast.makeText(this, "Please Select an Image", Toast.LENGTH_SHORT).show()
            isValid = false

        }
        return isValid
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE)
    }
    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.data // Get the image URI
            imageViewAddFriend.setImageURI(imageUri) // Display the image
        }
    }

    private fun insertFriendData() {
        val firstNameText = firstName.text.toString()
        val lastNameText = lastName.text.toString()
        val ageText = age.text.toString()
        val addressText = address.text.toString()

        // Get selected gender
        val selectedGenderId = genderGroup.checkedRadioButtonId
        val gender = findViewById<RadioButton>(selectedGenderId).text.toString()

        // Validate fields
        if (firstNameText.isBlank() || lastNameText.isBlank() || ageText.isBlank() || addressText.isBlank()) {
            Toast.makeText(this, "Please fill out all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Insert data into the database
        val friend = Friend(
            firstName = firstNameText,
            lastName = lastNameText,
            age = ageText.toInt(),
            address = addressText,
            gender = gender,
            imageUri = imageUri.toString()
        )

        // Insert data using coroutine
        CoroutineScope(Dispatchers.IO).launch {
            val db = FriendDatabase.getDatabase(applicationContext)
            db.friendDao().insertFriend(friend)

            // Switch back to the Main thread to show a message and navigate
            runOnUiThread {
                Toast.makeText(this@AddFriendActivity, "Friend added successfully!", Toast.LENGTH_SHORT).show()
                clearFields()

                // Navigate to FriendsActivity
                val intent = Intent(this@AddFriendActivity, FriendsActivity::class.java)
                startActivity(intent)
                finish() // Close this activity
            }
        }
    }

    private fun clearFields() {
        firstName.text.clear()
        lastName.text.clear()
        age.text.clear()
        address.text.clear()
        genderGroup.check(R.id.male) // Default back to male
        imageViewAddFriend.setImageResource(R.drawable.persons) // Set a default image

    }
    companion object {
        private const val PICK_IMAGE = 1
    }
}

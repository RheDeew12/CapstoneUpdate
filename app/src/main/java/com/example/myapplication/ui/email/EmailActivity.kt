package com.dicoding.pneumuvision.ui.email

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.dicoding.pneumuvision.R

class EmailActivity : AppCompatActivity() {

    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etMessage: EditText
    private lateinit var btnAttachFile: Button
    private lateinit var btnSendMessage: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email)

        // Initialize views
        etName = findViewById(R.id.et_name)
        etEmail = findViewById(R.id.et_email)
        etMessage = findViewById(R.id.et_message)
        btnAttachFile = findViewById(R.id.btn_attach_file)
        btnSendMessage = findViewById(R.id.btn_send_message)

        // Set click listener for send message button
        btnSendMessage.setOnClickListener {
            sendMessage()
        }
    }

    private fun sendMessage() {
        val name = etName.text.toString().trim()
        val email = etEmail.text.toString().trim()
        val message = etMessage.text.toString().trim()

        // Validate input fields
        if (name.isEmpty()) {
            etName.error = "Name is required"
            etName.requestFocus()
            return
        }

        if (email.isEmpty()) {
            etEmail.error = "Email is required"
            etEmail.requestFocus()
            return
        }

        if (message.isEmpty()) {
            etMessage.error = "Message is required"
            etMessage.requestFocus()
            return
        }

        // Perform action to send the message (e.g., send email, submit form, etc.)
        // Replace the toast message with your actual logic to send the message
        val toastMessage = "Message sent:\nName: $name\nEmail: $email\nMessage: $message"
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show()
    }
}

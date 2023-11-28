package com.example.storyapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class ForgotPassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val submitBtn = findViewById<Button>(R.id.submitBtn)

        submitBtn.setOnClickListener {

            val intent = Intent(this,OtpVerification::class.java)
            startActivity(intent)
        }


    }
}
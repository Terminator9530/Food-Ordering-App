package com.example.foodorderingapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class ForgotPassword : AppCompatActivity() {
    lateinit var btnNext: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        btnNext = findViewById(R.id.btnNext)

        btnNext.setOnClickListener {
            val intent = Intent(this@ForgotPassword, OTP::class.java)
            startActivity(intent)
            finish()
        }
    }
}
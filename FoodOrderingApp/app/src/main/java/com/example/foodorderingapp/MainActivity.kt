package com.example.foodorderingapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        val handler = Handler()
        handler.postDelayed(Runnable { //Write whatever to want to do after delay specified (1 sec)
            val intent = Intent(this@MainActivity, Login::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }
}
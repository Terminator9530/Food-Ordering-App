package com.example.foodorderingapp.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.room.Room
import com.example.foodorderingapp.R
import com.example.foodorderingapp.database.DishDatabase

class OrderPlaced : AppCompatActivity() {
    lateinit var btnOk: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_placed)
        btnOk = findViewById(R.id.btnOk)
        btnOk.setOnClickListener {
            EmptyDishes(this@OrderPlaced).execute().get()
            val intent = Intent(this@OrderPlaced, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    class EmptyDishes(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg p0: Void?): Boolean {
            val db = Room.databaseBuilder(context, DishDatabase::class.java, "dish-db").build()
            db.dishDao().deleteAllDish()
            return true
        }
    }
}
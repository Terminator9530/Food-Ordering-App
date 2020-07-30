package com.example.foodorderingapp.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookreader.utils.ConnectionManager
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.DishesRecyclerAdapter
import com.example.foodorderingapp.adapter.HomeRecyclerAdapter
import com.example.foodorderingapp.database.DishDatabase
import com.example.foodorderingapp.database.DishEntity
import com.example.foodorderingapp.model.Dish
import com.example.foodorderingapp.model.Restaurant
import org.json.JSONException

class Dishes : AppCompatActivity() {
    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DishesRecyclerAdapter
    var restaurentId: String? = "100"
    var restaurentName: String? = "100"
    lateinit var toolbar: Toolbar
    lateinit var btnCheckOut: Button
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var dishInfoList = arrayListOf<Dish>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dishes)
        btnCheckOut = findViewById(R.id.btnCheckOut)
        btnCheckOut.visibility = View.GONE
        progressLayout = findViewById(R.id.progressLayout)
        progressBar = findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        btnCheckOut.setOnClickListener {
            println("Place Order")
            val intent = Intent(this@Dishes, Cart::class.java)
            intent.putExtra("restaurant_id", restaurentId)
            intent.putExtra("restaurant_name", restaurentName)
            startActivity(intent)
        }
        if (intent != null) {
            restaurentId = intent.getStringExtra("restaurant_id")
            restaurentName = intent.getStringExtra("restaurant_name")
        } else {
            finish()
            Toast.makeText(this@Dishes, "Some Error Occured", Toast.LENGTH_SHORT).show()
        }
        if (restaurentId == "100" || restaurentName == "100") {
            finish()
            Toast.makeText(this@Dishes, "Some Error Occured", Toast.LENGTH_SHORT).show()
        }
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = restaurentName
        recyclerHome = findViewById(R.id.recyclerDish)
        layoutManager = LinearLayoutManager(this@Dishes)

        if (ConnectionManager().checkConnectivity(this@Dishes)) {
            // post request

            val queue = Volley.newRequestQueue(this@Dishes)

            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurentId"
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    // handle request
                    println("Response is $it")
                    try {
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {
                            val dataArray = data.getJSONArray("data")
                            for (i in 0 until dataArray.length()) {
                                val dishJsonObject = dataArray.getJSONObject(i)
                                val dishObject = Dish(
                                    dishJsonObject.getString("id"),
                                    dishJsonObject.getString("name"),
                                    dishJsonObject.getString("cost_for_one"),
                                    dishJsonObject.getString("restaurant_id")
                                )
                                dishInfoList.add(dishObject)
                            }
                            progressLayout.visibility = View.GONE
                            recyclerAdapter =
                                DishesRecyclerAdapter(this@Dishes, dishInfoList, { changeView() })
                            recyclerHome.adapter = recyclerAdapter
                            recyclerHome.layoutManager = layoutManager
                        } else {
                            Toast.makeText(
                                this@Dishes,
                                "Some Error occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            this@Dishes,
                            "Some unexpected error occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    // handle error
                    println("Error is $it")
                    Toast.makeText(this@Dishes, "volley error occured", Toast.LENGTH_SHORT)
                        .show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "590d13b4181c7b"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)
        } else {
            println("No Internet")
            val dialog = AlertDialog.Builder(this@Dishes)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingIntent)
                finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(this@Dishes)
            }
            dialog.create()
            dialog.show()
        }
    }

    fun changeView() {
        println(RetrieveDishes(this@Dishes).execute().get())
        if (RetrieveDishes(this@Dishes).execute().get().isEmpty()) {
            btnCheckOut.visibility = View.GONE
        } else {
            btnCheckOut.visibility = View.VISIBLE
        }
    }

    class RetrieveDishes(val context: Context) : AsyncTask<Void, Void, List<DishEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<DishEntity> {
            val db = Room.databaseBuilder(context, DishDatabase::class.java, "dish-db").build()
            return db.dishDao().getAllDishes()
        }

    }

    class EmptyDishes(val context: Context) : AsyncTask<Void, Void, Boolean>() {
        override fun doInBackground(vararg p0: Void?): Boolean {
            val db = Room.databaseBuilder(context, DishDatabase::class.java, "dish-db").build()
            db.dishDao().deleteAllDish()
            println(db.dishDao().getAllDishes())
            return true
        }

    }

    override fun onBackPressed() {
        val dialog = AlertDialog.Builder(this@Dishes)
        dialog.setTitle("Confirmation")
        dialog.setMessage("Going Back will reset cart items. Do you still want to proceed ?")
        dialog.setPositiveButton("Yes") { text, listener ->
            EmptyDishes(this@Dishes).execute().get()
            super.onBackPressed()
        }
        dialog.setNegativeButton("No") { text, listener ->

        }
        dialog.create()
        dialog.show()
    }
}
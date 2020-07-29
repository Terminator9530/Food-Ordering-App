package com.example.foodorderingapp.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.DishesRecyclerAdapter
import com.example.foodorderingapp.adapter.FAQRecyclerAdapter
import com.example.foodorderingapp.adapter.HomeRecyclerAdapter
import com.example.foodorderingapp.model.Dish
import com.example.foodorderingapp.model.FAQ
import com.example.foodorderingapp.model.Restaurant
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException

class Dishes : AppCompatActivity() {
    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DishesRecyclerAdapter
    var restaurentId: String? = "100"
    var restaurentName: String? = "100"
    lateinit var toolbar: Toolbar
    var dishInfoList = arrayListOf<Dish>()
    var check = arrayListOf<Boolean>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dishes)
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
                            check.add(true)
                        }
                        recyclerAdapter =
                            DishesRecyclerAdapter(this@Dishes, dishInfoList, check)
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
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}
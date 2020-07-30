package com.example.foodorderingapp.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.CartRecyclerAdapter
import com.example.foodorderingapp.database.DishDatabase
import com.example.foodorderingapp.database.DishEntity
import com.example.foodorderingapp.database.RestaurantEntity
import com.example.foodorderingapp.fragment.FavouriteFragment
import com.example.foodorderingapp.model.Dish
import com.example.foodorderingapp.model.Restaurant
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class Cart : AppCompatActivity() {
    var restaurentId: String? = "100"
    var restaurentName: String? = "100"
    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var toolbar: Toolbar
    lateinit var btnPlaceOrder: Button
    lateinit var sharedPreferences: SharedPreferences
    var billAmount = 0
    var dbDishList = listOf<DishEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.userDetails), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_cart)
        toolbar = findViewById(R.id.toolbar)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        btnPlaceOrder.setOnClickListener {
            val queue = Volley.newRequestQueue(this@Cart)
            val url = "http://13.235.250.119/v2/place_order/fetch_result/"
            val user_id = sharedPreferences.getString("user_id", "0")
            val restaurent_id = restaurentId
            val total_cost = billAmount
            val jsonArray = JSONArray()
            for (i in dbDishList) {
                val jsonObject = JSONObject()
                jsonObject.put("food_item_id", i.dish_id.toString())
                jsonArray.put(jsonObject)
            }

            val jsonParams = JSONObject()
            jsonParams.put("user_id", user_id)
            jsonParams.put("restaurant_id", restaurent_id)
            jsonParams.put("total_cost", total_cost.toString())
            jsonParams.put("food", jsonArray)
            println("Json Paramas : $jsonParams")
            val jsonRequest = object : JsonObjectRequest(
                Request.Method.POST, url, jsonParams,
                Response.Listener {
                    println("Response is $it")
                    val data = it.getJSONObject("data")
                    println(data)
                    try {
                        val success = data.getBoolean("success")
                        if (success) {
                            Toast.makeText(
                                this@Cart,
                                "Order Placed",
                                Toast.LENGTH_SHORT
                            ).show()
                            val intent = Intent(this@Cart, OrderPlaced::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            val errorMessage = data.getString("errorMessage")
                            Toast.makeText(
                                this@Cart,
                                errorMessage,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@Cart,
                            "Some unexpected error occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                },
                Response.ErrorListener {
                    Toast.makeText(this@Cart, "volley error occured", Toast.LENGTH_SHORT).show()
                }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content-type"] = "application/json"
                    headers["token"] = "590d13b4181c7b"
                    return headers
                }
            }
            queue.add(jsonRequest)
        }
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        recyclerCart = findViewById(R.id.recyclerCartItems)
        layoutManager = LinearLayoutManager(this@Cart)
        if (intent != null) {
            restaurentId = intent.getStringExtra("restaurant_id")
            restaurentName = intent.getStringExtra("restaurant_name")
        } else {
            finish()
            Toast.makeText(this@Cart, "Some Error Occured", Toast.LENGTH_SHORT).show()
        }
        if (restaurentId == "100" || restaurentName == "100") {
            finish()
            Toast.makeText(this@Cart, "Some Error Occured", Toast.LENGTH_SHORT).show()
        }
        dbDishList = RetrieveDishes(this@Cart).execute().get()
        var dishInfoList = arrayListOf<Dish>()
        for (i in dbDishList) {
            print(i)
            val dishObject = Dish(
                i.dish_id.toString(),
                i.restaurantName,
                i.restaurantPrice,
                i.restaurantId.toString()
            )
            dishInfoList.add(dishObject)
            billAmount += i.restaurantPrice.toInt()
        }
        btnPlaceOrder.text = "Place Order : Rs. " + billAmount.toString()
        recyclerAdapter =
            CartRecyclerAdapter(this@Cart, dishInfoList)
        recyclerCart.adapter = recyclerAdapter
        recyclerCart.layoutManager = layoutManager
    }

    class RetrieveDishes(val context: Context) : AsyncTask<Void, Void, List<DishEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<DishEntity> {
            val db = Room.databaseBuilder(context, DishDatabase::class.java, "dish-db").build()
            return db.dishDao().getAllDishes()
        }
    }
}
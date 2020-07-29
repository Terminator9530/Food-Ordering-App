package com.example.foodorderingapp.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.CartRecyclerAdapter
import com.example.foodorderingapp.database.DishDatabase
import com.example.foodorderingapp.database.DishEntity
import com.example.foodorderingapp.database.RestaurantEntity
import com.example.foodorderingapp.fragment.FavouriteFragment
import com.example.foodorderingapp.model.Dish
import com.example.foodorderingapp.model.Restaurant

class Cart : AppCompatActivity() {
    var restaurantName: String? = "100"
    lateinit var recyclerCart: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: CartRecyclerAdapter
    lateinit var toolbar: Toolbar
    lateinit var btnPlaceOrder: Button
    var billAmount = 0
    var dbDishList = listOf<DishEntity>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        toolbar = findViewById(R.id.toolbar)
        btnPlaceOrder = findViewById(R.id.btnPlaceOrder)
        btnPlaceOrder.setOnClickListener {
            Toast.makeText(this@Cart, "Order Placed", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@Cart, OrderPlaced::class.java)
            startActivity(intent)
            finish()
        }
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        recyclerCart = findViewById(R.id.recyclerCartItems)
        layoutManager = LinearLayoutManager(this@Cart)
        if (intent != null) {
            restaurantName = intent.getStringExtra("restaurant_name")
        } else {
            finish()
            Toast.makeText(this@Cart, "Some Error Occured", Toast.LENGTH_SHORT).show()
        }
        if (restaurantName == "100") {
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
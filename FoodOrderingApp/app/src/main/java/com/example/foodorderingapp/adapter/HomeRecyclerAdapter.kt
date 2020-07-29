package com.example.foodorderingapp.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodorderingapp.R
import com.example.foodorderingapp.database.RestaurantDatabase
import com.example.foodorderingapp.database.RestaurantEntity
import com.example.foodorderingapp.model.Restaurant
import com.squareup.picasso.Picasso

class HomeRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<Restaurant>
) :
    RecyclerView.Adapter<HomeRecyclerAdapter.HomeViewHolder>() {
    class HomeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtRestaurantName: TextView = view.findViewById(R.id.txtRestaurantName)
        val txtRestaurantCostPerPerson: TextView =
            view.findViewById(R.id.txtRestaurantCostPerPerson)
        val txtRestaurantRating: TextView = view.findViewById(R.id.txtRestaurantRating)
        val imgRestaurant: ImageView = view.findViewById(R.id.imgRestaurant)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
        val icHeart: TextView = view.findViewById(R.id.icHeart)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HomeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_home_single_row, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: HomeViewHolder,
        position: Int
    ) {
        val restaurant = itemList[position]
        holder.txtRestaurantName.text = restaurant.restaurantName
        holder.txtRestaurantCostPerPerson.text =
            "₹ " + restaurant.restaurantCostPerPerson + "/person"
        holder.txtRestaurantRating.text = restaurant.restaurantRating
        Picasso.get().load(restaurant.restaurantImage).error(R.drawable.app_logo)
            .into(holder.imgRestaurant)

        val restaurantEntity = RestaurantEntity(
            restaurant.restaurantId.toInt(),
            restaurant.restaurantName,
            restaurant.restaurantCostPerPerson,
            restaurant.restaurantRating,
            restaurant.restaurantImage
        )

        val checkFav = DBAsyncTask(context, restaurantEntity, 1).execute()
        val isFav = checkFav.get()
        if (isFav) {
            holder.icHeart.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_heart_filled,
                0,
                0,
                0
            );
        } else {
            holder.icHeart.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_heart_outline,
                0,
                0,
                0
            );
        }

        holder.icHeart.setOnClickListener {
            if (!DBAsyncTask(context, restaurantEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, restaurantEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Restaurant Added to Favourites", Toast.LENGTH_SHORT)
                        .show()
                    holder.icHeart.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_heart_filled,
                        0,
                        0,
                        0
                    );
                } else {
                    Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(context, restaurantEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Restaurant Removed to Favourites", Toast.LENGTH_SHORT)
                        .show()
                    holder.icHeart.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_heart_outline,
                        0,
                        0,
                        0
                    );
                } else {
                    Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    class DBAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        /*
            Mode 1 ->check db if Restaurant is fav or not
            Mode 2 ->save Restaurant into db as fav
            Mode 3 ->remove the fav Restaurant
        */

        val db =
            Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    // check whether Restaurant is fav
                    val book: RestaurantEntity? = db.restaurantDao()
                        .getRestaurantById(restaurantEntity.restaurant_id.toString())
                    db.close()
                    return book != null
                }
                2 -> {
                    // add Restaurant to fav
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 -> {
                    // remove Restaurant from fav
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}
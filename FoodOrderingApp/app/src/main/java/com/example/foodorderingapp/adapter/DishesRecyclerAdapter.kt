package com.example.foodorderingapp.adapter

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodorderingapp.R
import com.example.foodorderingapp.database.DishDatabase
import com.example.foodorderingapp.database.DishEntity
import com.example.foodorderingapp.database.RestaurantDatabase
import com.example.foodorderingapp.database.RestaurantEntity
import com.example.foodorderingapp.model.Dish
import com.example.foodorderingapp.model.FAQ

class DishesRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<Dish>,
    var check: ArrayList<Boolean>
) :
    RecyclerView.Adapter<DishesRecyclerAdapter.DishesViewHolder>() {
    class DishesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCount: TextView = view.findViewById(R.id.txtCount)
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishCostPerPlate: TextView = view.findViewById(R.id.txtDishCostPerPlate)
        val btnAddOrRemove: Button = view.findViewById(R.id.btnAddOrRemove)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DishesViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_dish_single_row, parent, false)
        return DishesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: DishesViewHolder,
        position: Int
    ) {
        val dish = itemList[position]
        holder.txtCount.text = dish.dishId
        holder.txtDishName.text = dish.dishName
        holder.txtDishCostPerPlate.text = "₹ " + dish.dishCostPerPlate

        val dishEntity = DishEntity(
            dish.dishId.toInt(),
            dish.dishName,
            dish.dishCostPerPlate,
            dish.restaurantId.toInt()
        )

        val checkFav = DBAsyncTask(context, dishEntity, 1).execute()
        val isFav = checkFav.get()
        if (isFav) {
            holder.btnAddOrRemove.text = "Remove"
        } else {
            holder.btnAddOrRemove.text = "Add"
        }

        holder.btnAddOrRemove.setOnClickListener {
            if (!DBAsyncTask(context, dishEntity, 1).execute().get()) {
                val async = DBAsyncTask(context, dishEntity, 2).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Dish Added", Toast.LENGTH_SHORT)
                        .show()
                    holder.btnAddOrRemove.text = "Remove"
                } else {
                    Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT).show()
                }
            } else {
                val async = DBAsyncTask(context, dishEntity, 3).execute()
                val result = async.get()
                if (result) {
                    Toast.makeText(context, "Dish Removed", Toast.LENGTH_SHORT)
                        .show()
                    holder.btnAddOrRemove.text = "Add"
                } else {
                    Toast.makeText(context, "Some Error Occured", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    class DBAsyncTask(val context: Context, val dishEntity: DishEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        /*
            Mode 1 ->check db if Restaurant is fav or not
            Mode 2 ->save Restaurant into db as fav
            Mode 3 ->remove the fav Restaurant
        */

        val db =
            Room.databaseBuilder(context, DishDatabase::class.java, "dish-db").build()

        override fun doInBackground(vararg p0: Void?): Boolean {
            when (mode) {
                1 -> {
                    // check whether Restaurant is fav
                    val dish: DishEntity? = db.dishDao()
                        .getDishById(dishEntity.dish_id.toString())
                    db.close()
                    return dish != null
                }
                2 -> {
                    // add Restaurant to fav
                    db.dishDao().insertDish(dishEntity)
                    db.close()
                    return true
                }
                3 -> {
                    // remove Restaurant from fav
                    db.dishDao().deleteDish(dishEntity)
                    db.close()
                    return true
                }
                4 -> {
                    // empty data
                    db.dishDao().emptyDish()
                    db.close()
                    return true
                }
            }
            return false
        }

    }
}
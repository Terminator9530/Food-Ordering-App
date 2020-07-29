package com.example.foodorderingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.model.Dish
import com.example.foodorderingapp.model.FAQ

class DishesRecyclerAdapter(val context: Context, val itemList: ArrayList<Dish>) :
    RecyclerView.Adapter<DishesRecyclerAdapter.DishesViewHolder>() {
    class DishesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtCount: TextView = view.findViewById(R.id.txtCount)
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishCostPerPlate: TextView = view.findViewById(R.id.txtDishCostPerPlate)
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
    }
}
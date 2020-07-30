package com.example.foodorderingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.database.DishEntity
import com.example.foodorderingapp.model.Dish

class CartRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<Dish>
) :
    RecyclerView.Adapter<CartRecyclerAdapter.CartViewHolder>() {
    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtDishName: TextView = view.findViewById(R.id.txtDishName)
        val txtDishPrice: TextView = view.findViewById(R.id.txtDishPrice)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_cart_single_row, parent, false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val dish = itemList[position]
        println(dish)
        holder.txtDishName.text = dish.dishName
        holder.txtDishPrice.text = "Rs. " + dish.dishCostPerPlate
    }
}
package com.example.foodorderingapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodorderingapp.R
import com.example.foodorderingapp.database.DishEntity
import com.example.foodorderingapp.model.Dish
import com.example.foodorderingapp.model.Order

class OrderRecyclerAdapter(
    val context: Context,
    val itemList: ArrayList<Order>
) :
    RecyclerView.Adapter<OrderRecyclerAdapter.OrderViewHolder>() {
    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtDesc: TextView = view.findViewById(R.id.txtDesc)
        val llContent: LinearLayout = view.findViewById(R.id.llContent)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): OrderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_order_single_row, parent, false)
        return OrderViewHolder(view)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(
        holder: OrderViewHolder,
        position: Int
    ) {
        val dish = itemList[position]
        println(dish)
        holder.txtName.text = dish.column1
        holder.txtDesc.text = dish.column2
        if (dish.column2.contains("-", ignoreCase = true)) {
            val favColor = ContextCompat.getColor(context, R.color.appTheme)
            val textColor = ContextCompat.getColor(context, R.color.remove)
            holder.llContent.setBackgroundColor(favColor)
            holder.txtName.setTextColor(textColor)
        }
    }
}
package com.example.foodorderingapp.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.HomeRecyclerAdapter
import com.example.foodorderingapp.database.RestaurantDatabase
import com.example.foodorderingapp.database.RestaurantEntity
import com.example.foodorderingapp.model.Restaurant
import org.json.JSONObject


class FavouriteFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar
    var dbRestaurantList = listOf<RestaurantEntity>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE
        dbRestaurantList = RetrieveFavourites(activity as Context).execute().get()
        var restaurantInfoList = arrayListOf<Restaurant>()
        for (i in dbRestaurantList) {
            print(i)
            val restaurantObject = Restaurant(
                i.restaurant_id.toString(),
                i.restaurantName,
                i.restaurantPrice,
                i.restaurantRating,
                i.restaurantImage
            )
            restaurantInfoList.add(restaurantObject)
        }

        if (dbRestaurantList != null && activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = HomeRecyclerAdapter(activity as Context, restaurantInfoList)
            recyclerHome.adapter = recyclerAdapter
            recyclerHome.layoutManager = layoutManager
        }
        return view
    }

    class RetrieveFavourites(val context: Context) :
        AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg p0: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant-db")
                .build()
            return db.restaurantDao().getAllRestaurants()
        }

    }

}
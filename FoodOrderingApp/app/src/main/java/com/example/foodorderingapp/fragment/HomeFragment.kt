package com.example.foodorderingapp.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodorderingapp.R
import com.example.foodorderingapp.adapter.HomeRecyclerAdapter
import com.example.foodorderingapp.model.Restaurant
import org.json.JSONException

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerHome = view.findViewById(R.id.recyclerHome)
        layoutManager = LinearLayoutManager(activity)


        // post request

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
        val jsonObjectRequest =
            object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                // handle request
                println("Response is $it")
                try {
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val dataArray = data.getJSONArray("data")
                        var restaurantInfoList = arrayListOf<Restaurant>()
                        for (i in 0 until dataArray.length()) {
                            val restaurantJsonObject = dataArray.getJSONObject(i)
                            val restaurantObject = Restaurant(
                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("image_url")
                            )
                            restaurantInfoList.add(restaurantObject)
                        }
                        var check = arrayListOf<Boolean>()
                        for (i in 0 until dataArray.length()) {
                            check.add(false)
                        }
                        recyclerAdapter =
                            HomeRecyclerAdapter(activity as Context, restaurantInfoList, check)
                        recyclerHome.adapter = recyclerAdapter
                        recyclerHome.layoutManager = layoutManager
                    } else {
                        Toast.makeText(
                            activity as Context,
                            "Some Error occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } catch (e: JSONException) {
                    Toast.makeText(
                        activity as Context,
                        "Some unexpected error occured",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }, Response.ErrorListener {
                // handle error
                println("Error is $it")
                Toast.makeText(activity as Context, "volley error occured", Toast.LENGTH_SHORT)
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
        return view
    }

}
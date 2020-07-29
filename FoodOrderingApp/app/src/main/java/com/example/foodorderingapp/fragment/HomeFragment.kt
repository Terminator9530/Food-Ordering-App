package com.example.foodorderingapp.fragment

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodorderingapp.R
import com.example.foodorderingapp.activity.Login
import com.example.foodorderingapp.adapter.HomeRecyclerAdapter
import com.example.foodorderingapp.model.Restaurant
import org.json.JSONException
import java.util.*
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: HomeRecyclerAdapter
    var restaurantInfoList = arrayListOf<Restaurant>()
    var itemSelected: Int = -1

    var ratingComparator = Comparator<Restaurant> { res1, res2 ->
        if (res1.restaurantRating.compareTo(res2.restaurantRating, true) == 0) {
            // sort according to name if rating is same
            res1.restaurantName.compareTo(res2.restaurantName, true)
        } else {
            // else sort according to rating
            res1.restaurantRating.compareTo(res2.restaurantRating, true)
        }
    }

    var priceComparatorLowToHigh = Comparator<Restaurant> { res1, res2 ->
        if (res1.restaurantCostPerPerson.compareTo(res2.restaurantCostPerPerson, true) == 0) {
            // sort according to name if rating is same
            res1.restaurantName.compareTo(res2.restaurantName, true)
        } else {
            // else sort according to rating
            res1.restaurantCostPerPerson.compareTo(res2.restaurantCostPerPerson, true)
        }
    }

    var priceComparatorHighToLow = Comparator<Restaurant> { res1, res2 ->
        if (res1.restaurantCostPerPerson.compareTo(res2.restaurantCostPerPerson, true) == 0) {
            // sort according to name if rating is same
            res1.restaurantName.compareTo(res2.restaurantName, true)
        } else {
            // else sort according to rating
            res1.restaurantCostPerPerson.compareTo(res2.restaurantCostPerPerson, true)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        setHasOptionsMenu(true)
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
                        for (i in 0 until dataArray.length()) {
                            val restaurantJsonObject = dataArray.getJSONObject(i)
                            val restaurantObject = Restaurant(
                                restaurantJsonObject.getString("id"),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("cost_for_one"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("image_url")
                            )
                            restaurantInfoList.add(restaurantObject)
                        }
                        recyclerAdapter =
                            HomeRecyclerAdapter(activity as Context, restaurantInfoList)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater?.inflate(R.menu.menu_home, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item?.itemId
        if (id == R.id.action_sort) {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Sort By ?")
            val listItems = arrayOf("Cost(Low to High)", "Cost(High to Low)", "Rating")
            dialog.setSingleChoiceItems(listItems, itemSelected) { dialogInterface, i ->
                itemSelected = i
            }
            dialog.setPositiveButton("Yes") { text, listener ->
                if (itemSelected == 0) {
                    Collections.sort(restaurantInfoList, priceComparatorLowToHigh)
                }
                if (itemSelected == 1) {
                    Collections.sort(restaurantInfoList, priceComparatorHighToLow)
                    restaurantInfoList.reverse()
                }
                if (itemSelected == 2) {
                    Collections.sort(restaurantInfoList, ratingComparator)
                    restaurantInfoList.reverse()
                }
                recyclerAdapter.notifyDataSetChanged()
            }
            dialog.setNegativeButton("No") { text, listener ->

            }
            dialog.create()
            dialog.show()
        }
        return super.onOptionsItemSelected(item)
    }

}
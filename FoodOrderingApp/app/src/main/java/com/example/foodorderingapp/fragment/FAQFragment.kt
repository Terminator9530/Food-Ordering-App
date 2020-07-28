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
import com.example.foodorderingapp.adapter.FAQRecyclerAdapter
import com.example.foodorderingapp.adapter.HomeRecyclerAdapter
import com.example.foodorderingapp.model.FAQ
import com.example.foodorderingapp.model.Restaurant
import org.json.JSONException
import org.json.JSONObject

class FAQFragment : Fragment() {

    lateinit var recyclerHome: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: FAQRecyclerAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_f_a_q, container, false)
        recyclerHome = view.findViewById(R.id.recyclerFAQ)
        layoutManager = LinearLayoutManager(activity)
        var faqInfoList = arrayListOf<FAQ>(
            FAQ(
                "Can I add multiple dishes from different restaurants ?",
                "No, you can only add from one restaurant."
            ),
            FAQ(
                "How many plates can we order for one dish ?",
                "You can only add one plate per item."
            )
        )
        recyclerAdapter =
            FAQRecyclerAdapter(activity as Context, faqInfoList)
        recyclerHome.adapter = recyclerAdapter
        recyclerHome.layoutManager = layoutManager
        return view
    }

}
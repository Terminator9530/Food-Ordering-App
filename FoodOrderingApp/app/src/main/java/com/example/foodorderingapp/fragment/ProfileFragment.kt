package com.example.foodorderingapp.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foodorderingapp.R

class ProfileFragment(
    val name: String?,
    val mobileNumber: String?,
    val email: String?,
    val address: String?
) : Fragment() {
    lateinit var txtName: TextView
    lateinit var txtMobile: TextView
    lateinit var txtEmail: TextView
    lateinit var txtAddress: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        txtName = view.findViewById(R.id.txtName)
        txtMobile = view.findViewById(R.id.txtMobileNumber)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtAddress = view.findViewById(R.id.txtDeliveryAddress)
        txtName.text = name
        txtAddress.text = address
        txtEmail.text = email
        txtMobile.text = mobileNumber
        return view
    }

}
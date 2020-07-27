package com.example.foodorderingapp

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.lang.Exception

class Register : AppCompatActivity() {
    lateinit var etName: EditText
    lateinit var etEmail: EditText
    lateinit var etMobileNumber: EditText
    lateinit var etDeliveryAddress: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnRegister: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            /*
            1 ) Name should be min 3 chars long
            2) Password should be min 4 chars long
            */
            val name = etName.text.toString()
            val email = etEmail.text.toString()
            val mobileNumber = etMobileNumber.text.toString()
            val deliveryAddress = etDeliveryAddress.text.toString()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            if (password == confirmPassword) {
                val queue = Volley.newRequestQueue(this@Register)
                val url = "http://13.235.250.119/v2/register/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("name", name)
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("password", password)
                jsonParams.put("address", deliveryAddress)
                jsonParams.put("email", email)
                val jsonRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams,
                    Response.Listener {
                        println("Response is $it")
                        val data = it.getJSONObject("data")
                        println(data)
                        try {
                            val success = data.getBoolean("success")
                            if (success) {
                                Toast.makeText(
                                    this@Register,
                                    "Registered",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val errorMessage = data.getString("errorMessage")
                                Toast.makeText(
                                    this@Register,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@Register,
                                "Some unexpected error occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(this@Register, "volley error occured", Toast.LENGTH_SHORT)
                            .show()
                    }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "590d13b4181c7b"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            } else {
                Toast.makeText(this@Register, "Password Does not match", Toast.LENGTH_LONG).show()
            }
        }
    }
}
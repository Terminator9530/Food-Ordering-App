package com.example.foodorderingapp.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.bookreader.utils.ConnectionManager
import com.example.foodorderingapp.R
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception

class ForgotPassword : AppCompatActivity() {
    lateinit var btnNext: Button
    lateinit var etMobileNumber: EditText
    lateinit var etEmail: EditText
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.userDetails), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_forgot_password)
        btnNext = findViewById(R.id.btnNext)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        btnNext.setOnClickListener {
            val mobile = etMobileNumber.text.toString()
            val email = etEmail.text.toString()
            if (ConnectionManager().checkConnectivity(this@ForgotPassword)) {
                val queue = Volley.newRequestQueue(this@ForgotPassword)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobile)
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
                                sharedPreferences.edit().putString("mobile", mobile).apply()
                                val intent = Intent(this@ForgotPassword, OTP::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val errorMessage = data.getString("errorMessage")
                                Toast.makeText(
                                    this@ForgotPassword,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@ForgotPassword,
                                "Some unexpected error occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(
                            this@ForgotPassword,
                            "volley error occured",
                            Toast.LENGTH_SHORT
                        )
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
                println("No Internet")
                val dialog = AlertDialog.Builder(this@ForgotPassword)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@ForgotPassword)
                }
                dialog.create()
                dialog.show()
            }
        }
    }
}
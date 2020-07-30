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

class OTP : AppCompatActivity() {
    lateinit var etOTP: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnSubmit: Button
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.userDetails), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_otp_screen)
        etOTP = findViewById(R.id.etOTP)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnSubmit = findViewById(R.id.btnSubmit)

        btnSubmit.setOnClickListener {
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()
            val mobile = sharedPreferences.getString("mobile", "no mobile")
            val otp = etOTP.text.toString()
            if (password == confirmPassword) {
                if (ConnectionManager().checkConnectivity(this@OTP)) {
                    val queue = Volley.newRequestQueue(this@OTP)
                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                    val jsonParams = JSONObject()
                    jsonParams.put("mobile_number", mobile)
                    jsonParams.put("password", password)
                    jsonParams.put("otp", otp)
                    val jsonRequest = object : JsonObjectRequest(
                        Request.Method.POST, url, jsonParams,
                        Response.Listener {
                            println("Response is $it")
                            val data = it.getJSONObject("data")
                            println(data)
                            try {
                                val success = data.getBoolean("success")
                                if (success) {
                                    val successMessage = data.getString("successMessage")
                                    Toast.makeText(
                                        this@OTP,
                                        successMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    val intent = Intent(this@OTP, Login::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val errorMessage = data.getString("errorMessage")
                                    Toast.makeText(
                                        this@OTP,
                                        errorMessage,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(
                                    this@OTP,
                                    "Some unexpected error occured",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        Response.ErrorListener {
                            Toast.makeText(this@OTP, "volley error occured", Toast.LENGTH_SHORT)
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
                    val dialog = AlertDialog.Builder(this@OTP)
                    dialog.setTitle("Error")
                    dialog.setMessage("Internet Connection is not Found")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this@OTP)
                    }
                    dialog.create()
                    dialog.show()
                }
            } else {
                Toast.makeText(
                    this@OTP,
                    "Password Does not match",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }
}
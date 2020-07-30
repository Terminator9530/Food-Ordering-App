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

class Login : AppCompatActivity() {
    lateinit var signUp: Button
    lateinit var forgotPassword: Button
    lateinit var btnLogin: Button
    lateinit var etMobileNumber: EditText
    lateinit var etPassword: EditText
    lateinit var sharedPreferences: SharedPreferences
    var isLoggedIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.userDetails), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)
        signUp = findViewById(R.id.signUp)
        forgotPassword = findViewById(R.id.forgotPassword)
        btnLogin = findViewById(R.id.btnLogin)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)

        signUp.setOnClickListener {
            val intent = Intent(this@Login, Register::class.java)
            startActivity(intent)
        }

        forgotPassword.setOnClickListener {
            val intent = Intent(this@Login, ForgotPassword::class.java)
            startActivity(intent)
        }

        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        if (isLoggedIn) {
            val intent = Intent(this@Login, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            val mobile = etMobileNumber.text.toString()
            val password = etPassword.text.toString()
            if (ConnectionManager().checkConnectivity(this@Login)) {
                val queue = Volley.newRequestQueue(this@Login)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobile)
                jsonParams.put("password", password)
                val jsonRequest = object : JsonObjectRequest(
                    Request.Method.POST, url, jsonParams,
                    Response.Listener {
                        println("Response is $it")
                        val data = it.getJSONObject("data")
                        println(data)
                        try {
                            val success = data.getBoolean("success")
                            if (success) {
                                val userDetails = data.getJSONObject("data")
                                saveSharedPreferences(userDetails)
                                Toast.makeText(
                                    this@Login,
                                    "Logged In",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this@Login, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                val errorMessage = data.getString("errorMessage")
                                Toast.makeText(
                                    this@Login,
                                    errorMessage,
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this@Login,
                                "Some unexpected error occured",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    Response.ErrorListener {
                        Toast.makeText(this@Login, "volley error occured", Toast.LENGTH_SHORT)
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
                val dialog = AlertDialog.Builder(this@Login)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection is not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val settingIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this@Login)
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    fun saveSharedPreferences(userDetails: JSONObject) {
        sharedPreferences.edit().putString("user_id", userDetails.getString("user_id")).apply()
        sharedPreferences.edit().putString("name", userDetails.getString("name")).apply()
        sharedPreferences.edit().putString("email", userDetails.getString("email")).apply()
        sharedPreferences.edit().putString("mobile_number", userDetails.getString("mobile_number"))
            .apply()
        sharedPreferences.edit().putString("address", userDetails.getString("address")).apply()
        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
    }
}
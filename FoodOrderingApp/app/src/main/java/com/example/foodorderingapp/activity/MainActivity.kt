package com.example.foodorderingapp.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodorderingapp.R
import com.example.foodorderingapp.fragment.*
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolBar: androidx.appcompat.widget.Toolbar
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    lateinit var sharedPreferences: SharedPreferences
    var previousMenuItem: MenuItem? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences =
            getSharedPreferences(getString(R.string.userDetails), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_main)

        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolBar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        openHome()

        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer
        )
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousMenuItem != null) {
                previousMenuItem?.isChecked = false
            }
            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it
            when (it.itemId) {
                R.id.home -> {
                    openHome()
                    Toast.makeText(this@MainActivity, "Clicked on Home", Toast.LENGTH_SHORT).show()
                }
                R.id.profile -> {
                    val name = sharedPreferences.getString("name", "John Doe")
                    val mobileNumber =
                        sharedPreferences.getString("mobile_number", "+91 - 9580374184")
                    val email = sharedPreferences.getString("email", "johndoe@gmail.com")
                    val address = sharedPreferences.getString("deliveryAddress", "Gurugram")
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            ProfileFragment(name, mobileNumber, email, address)
                        )
                        .addToBackStack("Profile")
                        .commit()
                    drawerLayout.closeDrawers()
                    setUpToolbar("My Profile")
                    Toast.makeText(this@MainActivity, "Clicked on Profile", Toast.LENGTH_SHORT)
                        .show()
                }
                R.id.favourite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FavouriteFragment()
                        )
                        .addToBackStack("Favourite")
                        .commit()
                    drawerLayout.closeDrawers()
                    setUpToolbar("Favourite Restaurants")
                    Toast.makeText(
                        this@MainActivity,
                        "Clicked on Favourite Restaurant",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            OrderFragment()
                        )
                        .addToBackStack("History")
                        .commit()
                    drawerLayout.closeDrawers()
                    setUpToolbar("My Previous Orders")
                    Toast.makeText(
                        this@MainActivity,
                        "Clicked on Order History",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction()
                        .replace(
                            R.id.frame,
                            FAQFragment()
                        )
                        .addToBackStack("FAQ")
                        .commit()
                    drawerLayout.closeDrawers()
                    setUpToolbar("Frequently Asked Questions")
                    Toast.makeText(this@MainActivity, "Clicked on FAQ's", Toast.LENGTH_SHORT).show()
                }
                R.id.logout -> {
                    Toast.makeText(this@MainActivity, "Clicked on LogOut", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun setUpToolbar(title: String) {
        setSupportActionBar(toolbar)
        supportActionBar?.title = title
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }

    fun openHome() {
        supportFragmentManager.beginTransaction().replace(
            R.id.frame,
            HomeFragment()
        ).addToBackStack("Home").commit()
        drawerLayout.closeDrawers()
        setUpToolbar("All Restaurants")
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when (frag) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()
        }
    }
}
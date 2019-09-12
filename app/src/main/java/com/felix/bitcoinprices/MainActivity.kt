package com.felix.bitcoinprices

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.androidnetworking.AndroidNetworking
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private val bottomNavListener = BottomNavigationView.OnNavigationItemSelectedListener {menuItem ->
        lateinit var selectedFragment: Fragment
        true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AndroidNetworking.initialize(applicationContext)

        val bottomNav : BottomNavigationView = findViewById(R.id.bottomNavBar)
        bottomNav.setOnNavigationItemSelectedListener(bottomNavListener)

        val toolbar: Toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val fragment: Fragment = ExchangeFragment()
        supportFragmentManager.commit { replace(R.id.fragmentContainer, fragment, fragment.javaClass.simpleName) }
        supportActionBar?.title = "Exchange"
    }
}

package com.felix.bitcoinprices

import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.androidnetworking.AndroidNetworking
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false

    private val bottomNavListener = BottomNavigationView.OnNavigationItemSelectedListener {menuItem ->
        lateinit var selectedFragment: Fragment

        when(menuItem.itemId){
            R.id.menu_exchange ->{
                selectedFragment = ExchangeFragment()
                supportActionBar?.title = resources.getString(R.string.exchange)
            }
            R.id.menu_calculator ->{
                selectedFragment = CalculatorFragment()
                supportActionBar?.title = "Calculator"
            }
        }
        supportFragmentManager.commit { replace(R.id.fragmentContainer, selectedFragment, selectedFragment.javaClass.simpleName) }
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

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Press back again to leave", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }


}

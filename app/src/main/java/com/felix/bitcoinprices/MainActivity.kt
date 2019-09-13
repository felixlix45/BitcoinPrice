package com.felix.bitcoinprices

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.felix.bitcoinprices.model.DBItems
import com.felix.bitcoinprices.model.Items
import com.felix.bitcoinprices.viewmodel.ItemsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONObject
import java.text.DecimalFormat

class MainActivity() : AppCompatActivity() {

    private var doubleBackToExitPressedOnce = false
    private lateinit var itemsViewModel: ItemsViewModel

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

//        getData()
        itemsViewModel = ItemsViewModel(application)
        itemsViewModel.getData()
        val bottomNav : BottomNavigationView = findViewById(R.id.bottomNavBar)
        bottomNav.setOnNavigationItemSelectedListener(bottomNavListener)

        val toolbar: Toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        val fragment: Fragment = ExchangeFragment()
        supportFragmentManager.commit { replace(R.id.fragmentContainer, fragment, fragment.javaClass.simpleName) }
        supportActionBar?.title = "Exchange"



    }

    fun getData(){
        try{
            AndroidNetworking.get("https://api.coinbase.com/v2/exchange-rates?currency=BTC")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject?) {
                        if(response!= null){
                            val dec = DecimalFormat("#,###.00")
                            val ratesObj = response.getJSONObject("data").getJSONObject("rates")

                            val keys: Iterator<String> = ratesObj.keys()
                            while (keys.hasNext()){
                                val items = Items()
                                val dbItems = DBItems()
                                val keyValue = keys.next()
                                items.name = keyValue
                                items.price = dec.format(ratesObj.getString(keyValue).toDouble())

                                dbItems.name = keyValue
                                dbItems.price = dec.format(ratesObj.getString(keyValue).toDouble())
                                itemsViewModel.insert(dbItems)
                            }
                        }
                    }

                    override fun onError(error: ANError) {
                        Log.i("ERROR", error.errorDetail)
                    }
                })
        }catch (e: Exception){
            Log.e(ItemsViewModel.TAG, e.toString())
        }
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

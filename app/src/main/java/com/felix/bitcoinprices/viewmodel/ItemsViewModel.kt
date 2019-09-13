package com.felix.bitcoinprices.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.felix.bitcoinprices.model.DBItems
import com.felix.bitcoinprices.model.Items
import com.felix.bitcoinprices.repository.ItemsRepository
import org.json.JSONObject
import java.text.DecimalFormat

class ItemsViewModel(application: Application) : AndroidViewModel(application) {

    private var listItems:MutableLiveData<ArrayList<Items>> = MutableLiveData()

    private var repository = ItemsRepository(application)
    private var listDBItems: LiveData<List<DBItems>>

    init {
        listDBItems = repository.getAll()
    }

    fun insert(items: DBItems){
        repository.insert(items)
    }

    fun deleteAll(){
        repository.deleteAll()
    }

    fun getAllItems() : LiveData<List<DBItems>>{
        return listDBItems
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
                                repository.insert(dbItems)
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

    companion object{
        const val TAG = "ItemsViewModel"
    }
}
package com.felix.bitcoinprices.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.OkHttpResponseAndJSONObjectRequestListener
import com.felix.bitcoinprices.model.DBItems
import com.felix.bitcoinprices.repository.ItemsRepository
import okhttp3.Response
import org.json.JSONObject
import java.text.DecimalFormat

class ItemsViewModel(application: Application) : AndroidViewModel(application) {

    private var repository = ItemsRepository(application)
    private var listDBItems: LiveData<List<DBItems>>

    init {
        listDBItems = repository.getAll()
    }

    fun getAllItems(): LiveData<List<DBItems>> {
        return listDBItems
    }

    //Lebih mempunyai banyak pilhan kurs mata uang, tapi refresh rate 15 menit sekali
    fun getData() {
        try {
            AndroidNetworking.get("https://api.coinbase.com/v2/exchange-rates?currency=BTC")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsOkHttpResponseAndJSONObject(object :
                    OkHttpResponseAndJSONObjectRequestListener {
                    override fun onResponse(okHttpResponse: Response?, response: JSONObject?) {
                        if (response != null) {
                            val dec = DecimalFormat("#,###.00")
                            val ratesObj = response.getJSONObject("data").getJSONObject("rates")

                            val keys: Iterator<String> = ratesObj.keys()

                            while (keys.hasNext()) {
                                val dbItems = DBItems()
                                val keyValue = keys.next()

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
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    fun getDataBlockchain() {
        try {
            AndroidNetworking.get("https://blockchain.info/ticker")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsOkHttpResponseAndJSONObject(object :
                    OkHttpResponseAndJSONObjectRequestListener {
                    override fun onResponse(okHttpResponse: Response?, response: JSONObject?) {
                        if (response != null) {
                            repository.deleteAll()
                            val dec = DecimalFormat("#,###.00")
//                            val ratesObj = response.getJSONObject("data").getJSONObject("rates")

                            val keys: Iterator<String> = response.keys()
                            while (keys.hasNext()) {
                                val dbItems = DBItems()
                                val keyValue = keys.next()

                                dbItems.name = keyValue
                                dbItems.price =
                                    dec.format(response.getJSONObject(keyValue).getString("last").toDouble())
                                repository.insert(dbItems)
                            }
                        }
                    }

                    override fun onError(error: ANError) {
                        Log.i("ERROR", error.errorDetail)
                    }
                })
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    companion object {
        const val TAG = "ItemsViewModel"
    }
}
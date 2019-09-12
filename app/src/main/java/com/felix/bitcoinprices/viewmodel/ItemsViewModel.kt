package com.felix.bitcoinprices.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.felix.bitcoinprices.model.Items
import org.json.JSONObject
import java.text.DecimalFormat

class ItemsViewModel : ViewModel() {

    private var listItems:MutableLiveData<ArrayList<Items>> = MutableLiveData()

    fun setItems(){
        val tempList = ArrayList<Items>()
        try{
            AndroidNetworking.get("https://api.coinbase.com/v2/exchange-rates?currency=BTC")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener{
                    override fun onResponse(response: JSONObject?) {
                        if(response!= null){
                            val dec = DecimalFormat("#,###.00")
                            val ratesObj = response.getJSONObject("data").getJSONObject("rates")
                            if(ratesObj!=null){
                                val keys: Iterator<String> = ratesObj.keys()
                                while (keys.hasNext()){
                                    val items = Items()
                                    val keyValue = keys.next()
                                    items.name = keyValue
                                    items.price = dec.format(ratesObj.getString(keyValue).toDouble())
                                    tempList.add(items)
                                }

                                listItems.postValue(tempList)
                            }else{
                                Log.i("ViewModel", "Response is Null")
                            }

                        }else{
                            listItems.postValue(null)
                        }
                    }

                    override fun onError(anError: ANError?) {

                    }
                })
        }catch (e: Exception){
            Log.e(TAG, e.toString())
        }
    }

    fun getItems() : LiveData<ArrayList<Items>>{
        return listItems
    }

    companion object{
        const val TAG = "ItemsViewModel"
    }
}
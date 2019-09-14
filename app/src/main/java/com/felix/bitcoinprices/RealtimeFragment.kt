package com.felix.bitcoinprices

import android.content.Context.MODE_PRIVATE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import okhttp3.*
import org.json.JSONObject
import java.text.DecimalFormat
import java.util.concurrent.TimeUnit

class RealtimeFragment : Fragment() {

    private lateinit var webSocket: WebSocket
    private lateinit var txtPriceRealtime: TextView

    private lateinit var etUSD: EditText
    private lateinit var txtUSDtoBTC: TextView

    private lateinit var newPrice: String
    private lateinit var price: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_realtime, container, false)

        txtPriceRealtime = v.findViewById(R.id.tvPriceRealtime)
        txtPriceRealtime.text = resources.getString(R.string._1_btc_equals, "         ")

        etUSD = v.findViewById(R.id.etValueUSD)
        txtUSDtoBTC = v.findViewById(R.id.tvUSDtoBTC)
        txtUSDtoBTC.text = resources.getString(R.string.usd_to_btc, "0")
        etUSD.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0 == "0" || p0?.length == 0) {
                    txtUSDtoBTC.text = resources.getString(R.string.usd_to_btc, "0")
                } else {
                    val dec = DecimalFormat("#,###.000000000")
                    val btc: Double = price.toDouble()
                    val usd: Double = p0.toString().toDouble()
                    val newResult: String = dec.format(((usd / btc).toString()).toDouble())
                    txtUSDtoBTC.text = resources.getString(R.string.usd_to_btc, newResult)
                }


            }
        })
        return v
    }

    override fun onStart() {
        super.onStart()
        connectWebSocket()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop()")
        disconnectWebSocket()
        val sharedPreferences = activity?.getSharedPreferences("BTCValue", MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.putString("USDtoBTC", txtPriceRealtime.text.toString())
        editor?.apply()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG, "onActivityCreated()")
        val sharedPreferences = activity?.getSharedPreferences("BTCValue", MODE_PRIVATE)
        val data = sharedPreferences?.getString("USDtoBTC", txtPriceRealtime.text.toString())
        Log.d(tag, data.toString())
        txtPriceRealtime.text = data

    }

    private fun connectWebSocket() {
        val client = OkHttpClient.Builder()
            .readTimeout(0, TimeUnit.MILLISECONDS)
            .build()

        val request = Request.Builder()
            .url("wss://ws-feed.pro.coinbase.com")
            .build()
        webSocket = client.newWebSocket(request, getWebSocketListener())
    }

    private fun subscribe() {
        webSocket.send(
            "{\n" +
                    "    \"type\": \"subscribe\",\n" +
                    "    \"channels\": [{ \"name\": \"ticker\", \"product_ids\": [\"BTC-USD\"] }]\n" +
                    "}"
        )
    }

    private fun disconnectWebSocket() {
        webSocket.cancel()
    }


    private fun getWebSocketListener(): WebSocketListener {
        return object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                Log.d(TAG, "onOpen")
                subscribe()
            }

            override fun onMessage(webSocket: WebSocket?, text: String?) {
                activity?.runOnUiThread {
                    Log.d(TAG, "onMessage :$text ")
                    if (text != null) {
                        try {
                            val jsonObject = JSONObject(text)
                            price = jsonObject.getString("price")
                            val dec = DecimalFormat("#,###.00")
                            newPrice = dec.format(price.toDouble())
                            Log.d(TAG, newPrice)

                            txtPriceRealtime.text =
                                resources.getString(R.string._1_btc_equals, newPrice)

                            if (etUSD.text.toString().isNotEmpty()) {
                                val dec2 = DecimalFormat("#,###.00000000")
                                val btc: Double = price.toDouble()
                                val usd: Double = etUSD.text.toString().toDouble()
                                val newResult: String =
                                    dec2.format(((usd / btc).toString()).toDouble())
                                txtUSDtoBTC.text =
                                    resources.getString(R.string.usd_to_btc, newResult)
                            } else {
                                txtUSDtoBTC.text = resources.getString(R.string.usd_to_btc, "0")
                            }

                        } catch (e: Exception) {
                            Log.e(TAG, e.toString())
                        }
                    } else {
                        txtPriceRealtime.text = getString(R.string.no_data_available)
                    }


                }
            }

            override fun onClosing(webSocket: WebSocket?, code: Int, reason: String?) {
                webSocket!!.close(1000, null)
                activity?.runOnUiThread {
                    Log.d(TAG, "onClose")
                }
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                t.printStackTrace()
            }
        }
    }

    companion object {
        const val TAG = "RealtimeFragment"
    }

}

package com.felix.bitcoinprices

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.felix.bitcoinprices.adapter.ItemsAdapter
import com.felix.bitcoinprices.model.Items
import com.felix.bitcoinprices.viewmodel.ItemsViewModel
import com.google.android.material.snackbar.Snackbar

class ExchangeFragment : Fragment() {

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var itemsViewModel: ItemsViewModel
    private lateinit var rvItems: RecyclerView
    private lateinit var svItems: SearchView
    private lateinit var shimmerContainer: ShimmerFrameLayout
    private lateinit var progressBar: ProgressBar

    private val svListener = (object : SearchView.OnQueryTextListener{
        override fun onQueryTextSubmit(p0: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            itemsAdapter.filter.filter(p0)
            return true
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_exchange, container, false)

        rvItems = v.findViewById(R.id.rvExchangeItem)
        svItems = v.findViewById(R.id.svItems)
        shimmerContainer = v.findViewById(R.id.shimmerContainer)
        progressBar = v.findViewById(R.id.pBar)
        progressBar.visibility = View.GONE

        svItems.setOnQueryTextListener(svListener)

        itemsAdapter = ItemsAdapter(requireActivity())
        itemsViewModel = ViewModelProviders.of(requireActivity()).get(ItemsViewModel::class.java)
        itemsViewModel.getItems().observe(viewLifecycleOwner, Observer<ArrayList<Items>>{listItems ->
            val noInternetSnackbar = Snackbar.make(activity!!.findViewById(R.id.coordinatorFragment), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
            if(listItems[0].name.equals("connectionError")){
                noInternetSnackbar.show()
                Handler().postDelayed({
                    Log.i(tag, "Fetch data (No Internet)")
                    itemsViewModel.setItems()
                    noInternetSnackbar.dismiss()
                },10000)
            }else if(listItems.isNotEmpty()){

                itemsAdapter.setData(listItems)
                shimmerContainer.stopShimmer()
                shimmerContainer.visibility = View.GONE
                progressBar.visibility = View.GONE
                Handler().postDelayed({
                    progressBar.visibility = View.VISIBLE
                    Log.i(tag, "Fetch data")
                    itemsViewModel.setItems()
                },15000)

            }else{
                Snackbar.make(activity!!.findViewById(R.id.coordinatorFragment), "No Data Available", Snackbar.LENGTH_INDEFINITE).show()
            }
        })

        loadData()
        buildRecyclerView()
        return v

    }

    private fun loadData(){
            itemsViewModel.setItems()
            itemsAdapter = ItemsAdapter(requireActivity())
            itemsAdapter.notifyDataSetChanged()

    }

    private fun buildRecyclerView(){
        rvItems.layoutManager = LinearLayoutManager(requireActivity())
        rvItems.adapter = itemsAdapter
    }
}

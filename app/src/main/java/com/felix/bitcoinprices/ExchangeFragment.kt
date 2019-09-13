package com.felix.bitcoinprices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.felix.bitcoinprices.adapter.DBItemsAdapter
import com.felix.bitcoinprices.adapter.ItemsAdapter
import com.felix.bitcoinprices.model.DBItems
import com.felix.bitcoinprices.viewmodel.ItemsViewModel

class ExchangeFragment : Fragment() {

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var dbItemsAdapter: DBItemsAdapter
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_exchange, container, false)

        rvItems = v.findViewById(R.id.rvExchangeItem)
        buildRecyclerView()

        svItems = v.findViewById(R.id.svItems)
        shimmerContainer = v.findViewById(R.id.shimmerContainer)
        progressBar = v.findViewById(R.id.pBar)
        progressBar.visibility = View.GONE

        svItems.setOnQueryTextListener(svListener)
        itemsAdapter = ItemsAdapter(requireActivity())

        itemsViewModel = ViewModelProviders.of(requireActivity()).get(ItemsViewModel::class.java)
        itemsViewModel.getAllItems().observe(requireActivity(), Observer<List<DBItems>> {listItems->
            if(listItems.isNotEmpty()){
                dbItemsAdapter.setData(listItems)
                dbItemsAdapter.notifyDataSetChanged()
                shimmerContainer.stopShimmer()
                shimmerContainer.visibility = View.GONE
                progressBar.visibility = View.GONE
            }else{
                Toast.makeText(requireActivity(), "DATA NULL", Toast.LENGTH_SHORT).show()
            }
        })

        return v

    }

    private fun buildRecyclerView(){
        rvItems.layoutManager = LinearLayoutManager(requireActivity())
        rvItems.setHasFixedSize(true)
        dbItemsAdapter = DBItemsAdapter(requireActivity())
        rvItems.adapter = dbItemsAdapter
    }
}

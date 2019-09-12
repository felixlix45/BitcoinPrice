package com.felix.bitcoinprices

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.felix.bitcoinprices.adapter.ItemsAdapter
import com.felix.bitcoinprices.model.Items
import com.felix.bitcoinprices.viewmodel.ItemsViewModel

class ExchangeFragment : Fragment() {

    private lateinit var itemsAdapter: ItemsAdapter
    private lateinit var itemsViewModel: ItemsViewModel
    private lateinit var rvItems: RecyclerView
    private lateinit var svItems: SearchView

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

        svItems.setOnQueryTextListener(svListener)

        itemsViewModel = ViewModelProviders.of(requireActivity()).get(ItemsViewModel::class.java)
        itemsViewModel.getItems().observe(this, Observer<ArrayList<Items>>{listItems ->
            if(listItems.isNotEmpty()){
                itemsAdapter.setData(listItems)

            }else{
                Toast.makeText(requireActivity(), "No Data", Toast.LENGTH_SHORT).show()
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

package com.felix.bitcoinprices

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.facebook.shimmer.ShimmerFrameLayout
import com.felix.bitcoinprices.adapter.DBItemsAdapter
import com.felix.bitcoinprices.model.DBItems
import com.felix.bitcoinprices.viewmodel.ItemsViewModel

class ExchangeFragment : Fragment() {

    private lateinit var dbItemsAdapter: DBItemsAdapter
    private lateinit var itemsViewModel: ItemsViewModel
    private lateinit var rvItems: RecyclerView
    private lateinit var svItems: SearchView
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var shimmerContainer: ShimmerFrameLayout

    private val svListener = (object : SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(p0: String?): Boolean {
            return true
        }

        override fun onQueryTextChange(p0: String?): Boolean {
            dbItemsAdapter.filter.filter(p0)
            return true
        }
    })

    private val swipeListener = SwipeRefreshLayout.OnRefreshListener {
        itemsViewModel.getDataBlockchain()

        //Simulate network loading
        Handler().postDelayed({
            dbItemsAdapter.notifyDataSetChanged()
            swipeContainer.isRefreshing = false
        }, 2000)

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_exchange, container, false)

        swipeContainer = v.findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener(swipeListener)

        rvItems = v.findViewById(R.id.rvExchangeItem)
        buildRecyclerView()

        svItems = v.findViewById(R.id.svItems)
        svItems.setOnQueryTextListener(svListener)

        shimmerContainer = v.findViewById(R.id.shimmerContainer)
        swipeContainer.isRefreshing = true

        itemsViewModel = ViewModelProviders.of(requireActivity()).get(ItemsViewModel::class.java)
        itemsViewModel.getAllItems()
            .observe(requireActivity(), Observer<List<DBItems>> { listItems ->
                if (listItems.isNotEmpty()) {
                    dbItemsAdapter.setData(listItems)
                    dbItemsAdapter.notifyDataSetChanged()

                    //Simulate network loading
                    Handler().postDelayed({
                        shimmerContainer.stopShimmer()
                        shimmerContainer.visibility = View.GONE
                        swipeContainer.isRefreshing = false
                    }, 1000)
                }
            })

        return v

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

    }

    private fun buildRecyclerView() {
        rvItems.layoutManager = LinearLayoutManager(requireActivity())
        rvItems.setHasFixedSize(true)
        dbItemsAdapter = DBItemsAdapter(requireActivity())
        rvItems.adapter = dbItemsAdapter

    }
}

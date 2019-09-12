package com.felix.bitcoinprices.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.felix.bitcoinprices.R
import com.felix.bitcoinprices.model.Items
import java.util.*
import kotlin.collections.ArrayList

class ItemsAdapter(private val context: Context) : RecyclerView.Adapter<ItemsAdapter.ViewHolder>(), Filterable {

    private var listItems = ArrayList<Items>()
    private var copyList = ArrayList<Items>()

    init {
        this.copyList = listItems
    }

    fun setData(items: ArrayList<Items>){
        listItems.clear()
        listItems.addAll(items)
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if(p0 == null || p0.isEmpty()){
                    listItems = copyList
                }else{
                    val filteredList =ArrayList<Items>()
                    val filterPattern: String =p0.toString().toLowerCase(Locale.ROOT).trim()
                    for(item:Items in copyList){
                        if(item.name!!.toLowerCase(Locale.ROOT).contains(filterPattern)){
                            filteredList.add(item)
                        }
                    }
                    listItems = filteredList

                }
                val results =FilterResults()
                results.values = listItems
                return results

            }

            override fun publishResults(p0: CharSequence?, p1: FilterResults?) {
                if(p1!= null){
                    listItems  = p1.values as  ArrayList<Items>
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(context).inflate(R.layout.item_exchange, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(listItems[position])
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

        private val txtName: TextView = itemView.findViewById(R.id.tvName)
        private val txtPrice: TextView = itemView.findViewById(R.id.tvPrice)

        fun bind(items: Items){
            txtName.text = items.name
            txtPrice.text = items.price
        }
    }
}
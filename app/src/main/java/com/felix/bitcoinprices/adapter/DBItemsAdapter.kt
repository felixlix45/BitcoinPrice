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
import com.felix.bitcoinprices.model.DBItems
import java.util.*
import kotlin.collections.ArrayList

class DBItemsAdapter(private val context: Context) : RecyclerView.Adapter<DBItemsAdapter.ViewHolder>(), Filterable {

    private var listItems :List<DBItems> = ArrayList()
    private var copyList :List<DBItems> = ArrayList()

    init {
        this.copyList = listItems
    }

    fun setData(items: List<DBItems>){
        this.listItems = items
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object: Filter(){
            override fun performFiltering(p0: CharSequence?): FilterResults {
                if(p0 == null || p0.isEmpty()){
                    listItems = copyList
                }else{
                    val filteredList: MutableList<DBItems> = mutableListOf()
                    val filterPattern: String =p0.toString().toLowerCase(Locale.ROOT).trim()
                    for(item:DBItems in copyList){
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
                    listItems  = p1.values as  List<DBItems>
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

        fun bind(items: DBItems){
            txtName.text = items.name
            txtPrice.text = items.price
        }
    }
}
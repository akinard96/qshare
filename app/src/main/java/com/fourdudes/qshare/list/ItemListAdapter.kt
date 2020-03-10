package com.fourdudes.qshare.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.fourdudes.qshare.R
import com.fourdudes.qshare.data.Item

class ItemListAdapter(private val items: List<Item>,
                      private val clickListener: (Item) -> Unit) : RecyclerView.Adapter<ItemHolder>() {

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = items[position]
        holder.bind(item, clickListener)
    }
}
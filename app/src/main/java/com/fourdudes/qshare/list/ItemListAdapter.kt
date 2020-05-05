package com.fourdudes.qshare.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.fourdudes.qshare.R
import com.fourdudes.qshare.data.Item

class ItemListAdapter(private val items: List<Item>,
                      private val itemListViewModel: ItemListViewModel,
                      private val clickListener: (Item) -> Unit) :
    RecyclerView.Adapter<ItemHolder>(), GestureControlHelper.ItemTouchHelperAdapter {

    private lateinit var attachedRecyclerView: RecyclerView

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_item, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = items[position]
        holder.bind(item, clickListener)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        attachedRecyclerView = recyclerView
    }

    override fun onItemDismiss(position: Int) {
        val context = attachedRecyclerView.context
        val listItem = items[position]
        if (listItem != null) {
            AlertDialog.Builder(context)
                .setTitle(R.string.confirm_delete)
                .setMessage(context.resources.getString(R.string.confirm_delete_message, listItem.name))
                .setIcon(R.drawable.ic_menu_delete)
                .setPositiveButton(android.R.string.yes) { _, _ ->
                    itemListViewModel.deleteItem(listItem.id)
                }
                .setNegativeButton(android.R.string.no) { _, _ -> notifyItemChanged(position) }
                .show()
        }
    }
}
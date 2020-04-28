package com.fourdudes.qshare.list

import android.media.Image
import android.view.View
import android.widget.ExpandableListView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.fourdudes.qshare.R
import com.fourdudes.qshare.data.Item
import kotlinx.android.synthetic.main.list_item_item.view.*
import org.w3c.dom.Text

class ItemHolder(val view: View) : RecyclerView.ViewHolder(view) {
    private lateinit var item: Item

    private val titleTextView: TextView = itemView.findViewById(R.id.list_item_item_title)
    private val descriptionTextView: TextView = itemView.findViewById(R.id.list_item_item_decription)
    private val sentRecImageView: ImageView = itemView.findViewById(R.id.list_item_item_sent_rec)
    private val shareImageView: ImageView = itemView.findViewById(R.id.list_item_item_image_share)
    private val qrImageView: ImageView = itemView.findViewById(R.id.list_item_item_image_qr)

    fun bind(item: Item, clickListener: (Item) -> Unit) {
        this.item = item

        itemView.setOnClickListener{ clickListener(this.item) }

        titleTextView.text = this.item.name
        descriptionTextView.text = this.item.description
        sentRecImageView.rotation = when(this.item.isSent){
            true -> 0.toFloat()
            false -> 180.toFloat()
        }

    }


}
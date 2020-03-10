package com.fourdudes.qshare.list

import androidx.lifecycle.ViewModel
import com.fourdudes.qshare.data.Item

class ItemListViewModel : ViewModel() {
    val items = mutableListOf<Item>()

    init {
        for (i in 0 until 5) {
            val item = Item()
            item.name = "Item #$i"
            item.isSent = i % 2 == 0
            item.description = when(item.isSent) {
                true -> "sent item"
                false -> "received item"
            }
            items += item
        }
    }
}
package com.fourdudes.qshare.list

import androidx.lifecycle.ViewModel
import com.fourdudes.qshare.data.Item
import com.fourdudes.qshare.data.ItemRepository

class ItemListViewModel(private val itemRepository: ItemRepository) : ViewModel() {
    val itemLiveData = itemRepository.getItems()

    fun addItem(item: Item) {
        itemRepository.addItem(item)
    }

    fun deleteItem() {
        itemRepository.deleteItem()
    }
}
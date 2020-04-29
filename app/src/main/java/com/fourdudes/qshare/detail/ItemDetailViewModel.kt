package com.fourdudes.qshare.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.fourdudes.qshare.data.Item
import com.fourdudes.qshare.data.ItemRepository
import java.util.*

class ItemDetailViewModel(private val itemRepository: ItemRepository)
    : ViewModel() {

    // UUID for lookup
    private val itemIdLiveData = MutableLiveData<UUID>()

    // Actual item
    var itemLiveData: LiveData<Item?> =
        Transformations.switchMap(itemIdLiveData) {itemId ->
            itemRepository.getItem(itemId)
        }


    fun loadItem(itemId: UUID) {
        itemIdLiveData.value = itemId
    }

//    private val crimeIdLiveData = MutableLiveData<UUID>()
//
//    var crimeLiveData:LiveData<Crime?> =
//        Transformations.switchMap(crimeIdLiveData) {crimeId ->
//            crimeRepository.getCrime(crimeId)
//        }
//
//    fun loadCrime(crimeId: UUID) {
//        crimeIdLiveData.value = crimeId
//    }

}
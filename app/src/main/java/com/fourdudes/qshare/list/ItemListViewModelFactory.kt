package com.fourdudes.qshare.list

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fourdudes.qshare.data.ItemRepository

class ItemListViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(ItemRepository::class.java)
            .newInstance(ItemRepository.getInstance(context))
    }
}
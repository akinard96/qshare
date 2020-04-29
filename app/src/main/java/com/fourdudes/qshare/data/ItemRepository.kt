package com.fourdudes.qshare.data

import android.content.Context
import androidx.lifecycle.LiveData
import java.util.*
import java.util.concurrent.Executors

class ItemRepository (private val itemDao: ItemDao) {
    private val executor = Executors.newSingleThreadExecutor()

    fun getItems(): LiveData<List<Item>> = itemDao.getItems()
    fun getItem(itemId: UUID): LiveData<Item?> = itemDao.getItem(itemId)

    fun addItem(item: Item) {
        executor.execute {
            itemDao.addItem(item)
        }
    }

    fun deleteItem() {
        executor.execute {
            itemDao.deleteItem()
        }
    }

    companion object {
        private var instance: ItemRepository? = null

        fun getInstance(context: Context): ItemRepository? {
            return instance ?: let {
                if (instance == null) {
                    val database =
                        ItemDatabase.getInstance(
                            context
                        )
                    instance =
                        ItemRepository(database.itemDao())
                }
                return instance
            }
        }
    }
}
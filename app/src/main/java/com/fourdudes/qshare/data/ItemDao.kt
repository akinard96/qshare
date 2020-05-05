package com.fourdudes.qshare.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import java.util.*

@Dao
interface ItemDao {

    @Query("SELECT * FROM item ORDER BY date DESC")
    fun getItems(): LiveData<List<Item>>

    @Query("SELECT * FROM item WHERE id=(:itemId)")
    fun getItem(itemId: UUID): LiveData<Item?>

    @Insert
    fun addItem(item: Item)

    @Query("DELETE FROM item")
    fun deleteItems()

    @Query("DELETE FROM item WHERE id=(:itemId)")
    fun deleteItem(itemId: UUID)
}
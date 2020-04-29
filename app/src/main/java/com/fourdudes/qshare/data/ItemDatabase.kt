package com.fourdudes.qshare.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

private const val DATABASE_NAME = "item-database"

@Database(entities = [Item::class], version = 1)
@TypeConverters(ItemTypeConverters::class)
abstract class ItemDatabase : RoomDatabase() {

    companion object {
        private var instance: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase {
            return instance ?: let {
                instance ?: Room.databaseBuilder(context,
                    ItemDatabase::class.java,
                    DATABASE_NAME
                ).build()
            }
        }
    }

    abstract fun itemDao(): ItemDao
}
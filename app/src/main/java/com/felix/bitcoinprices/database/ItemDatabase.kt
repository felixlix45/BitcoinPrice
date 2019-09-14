package com.felix.bitcoinprices.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.felix.bitcoinprices.dao.ItemsDao
import com.felix.bitcoinprices.model.DBItems

@Database(entities = [DBItems::class], version = 2, exportSchema = false)
abstract class ItemDatabase : RoomDatabase() {

    abstract fun itemsDao(): ItemsDao

    companion object {
        private var INSTANCE: ItemDatabase? = null

        fun getInstance(context: Context): ItemDatabase? {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDatabase::class.java,
                    "item_database"
                )
                    .fallbackToDestructiveMigration()

                    .build()
            }
            return INSTANCE
        }
    }

}
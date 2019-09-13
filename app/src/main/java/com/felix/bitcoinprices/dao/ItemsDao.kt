package com.felix.bitcoinprices.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.felix.bitcoinprices.model.DBItems

@Dao
interface ItemsDao {

    @Insert
    fun insert(items: DBItems)

    @Query("DELETE FROM items_table")
    fun deleteAll()

    @Query("SELECT * FROM ITEMS_TABLE")
    fun getAll() : LiveData<List<DBItems>>



}
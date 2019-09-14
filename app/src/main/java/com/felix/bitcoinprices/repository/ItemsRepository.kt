package com.felix.bitcoinprices.repository

import android.app.Application
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.felix.bitcoinprices.dao.ItemsDao
import com.felix.bitcoinprices.database.ItemDatabase
import com.felix.bitcoinprices.model.DBItems

class ItemsRepository(application: Application) {

    private var itemsDao: ItemsDao
    private var allItems: LiveData<List<DBItems>>

    init {
        val database = ItemDatabase.getInstance(application)
        itemsDao = database!!.itemsDao()
        allItems = itemsDao.getAll()

    }

    fun insert(item: DBItems) {
        InsertItemAsyncTask(itemsDao).execute(item)

    }

    fun deleteAll() {
        DeleteAllItemAsyncTask(itemsDao).execute()
    }

    fun getAll(): LiveData<List<DBItems>> {
        return allItems
    }


    companion object {
        class InsertItemAsyncTask(private val itemsDao: ItemsDao) :
            AsyncTask<DBItems, Void, Void>() {
            override fun doInBackground(vararg p0: DBItems): Void? {
                itemsDao.insert(p0[0])
                return null
            }
        }

        class DeleteAllItemAsyncTask(private val itemsDao: ItemsDao) :
            AsyncTask<DBItems, Void, Void>() {
            override fun doInBackground(vararg p0: DBItems): Void? {
                itemsDao.deleteAll()
                return null
            }
        }

    }
}
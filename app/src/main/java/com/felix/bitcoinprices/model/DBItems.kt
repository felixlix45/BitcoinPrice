package com.felix.bitcoinprices.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "items_table")
class DBItems {
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    var name :String? = null
    var price:String? = null
}
package com.xsolla.android.storesdkexample.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface VirtualItemDao {
    @Query("SELECT * FROM VirtualItem")
    fun getAll(): List<VirtualItem>

    @Update
    fun updateItem(item: VirtualItem): Int

    @Insert
    fun insertItem(item: VirtualItem)
}
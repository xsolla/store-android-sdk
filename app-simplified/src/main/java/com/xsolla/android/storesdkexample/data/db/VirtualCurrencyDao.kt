package com.xsolla.android.storesdkexample.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface VirtualCurrencyDao {
    @Query("SELECT * FROM VirtualCurrency")
    fun getAll(): List<VirtualCurrency>

    @Update
    fun updateCurrency(currency: VirtualCurrency): Int

    @Insert
    fun insertCurrency(currency: VirtualCurrency)
}
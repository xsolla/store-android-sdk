package com.xsolla.android.storesdkexample.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [VirtualCurrency::class, VirtualItem::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun virtualCurrencyDao(): VirtualCurrencyDao
    abstract fun virtualItemDao(): VirtualItemDao
}
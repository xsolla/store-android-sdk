package com.xsolla.android.storesdkexample.data.db

import android.content.Context
import androidx.room.Room

object DB {
    lateinit var context: Context
    val db by lazy {
        Room.databaseBuilder(
                context,
                AppDatabase::class.java, "db-inventory"
        ).build()
    }
}
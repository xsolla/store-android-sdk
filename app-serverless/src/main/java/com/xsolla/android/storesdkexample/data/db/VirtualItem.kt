package com.xsolla.android.storesdkexample.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["sku"], unique = true)])
data class VirtualItem(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "sku") val sku: String,
        @ColumnInfo(name = "amount") val amount: String
)
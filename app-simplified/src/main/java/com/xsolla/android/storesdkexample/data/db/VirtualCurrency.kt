package com.xsolla.android.storesdkexample.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["currency"], unique = true)])
data class VirtualCurrency(
        @PrimaryKey(autoGenerate = true) val id: Int = 0,
        @ColumnInfo(name = "currency") val currency: String,
        @ColumnInfo(name = "amount") val amount: String
)
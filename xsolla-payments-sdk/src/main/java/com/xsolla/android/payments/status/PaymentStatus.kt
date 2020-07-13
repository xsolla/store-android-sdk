package com.xsolla.android.payments.status

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentStatus(
        val status: String,
        val purchase: Purchase,
        val external_id: String
) : Parcelable

@Parcelize
data class Purchase(
        val virtual_items: List<VirtualItem>?
) : Parcelable

@Parcelize
data class VirtualItem(
        val sku: String,
        val quantity: String
) : Parcelable
package com.xsolla.android.store.entity.response.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class InventoryOption(
    val consumable: Consumable? = null,
    @SerializedName("expiration_period")
    val expirationPeriod: ExpirationPeriod? = null
) : Parcelable
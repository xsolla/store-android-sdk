package com.xsolla.android.store.entity.response.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize


@Parcelize
data class Consumable(
    @SerializedName("usages_count")
    val usagesCount: Long? = null
) : Parcelable
package com.xsolla.android.store.entity.response.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Content(
    val sku: String? = null,
    val name: String? = null,
    val type: String? = null,
    val description: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null,
    val quantity: String? = null
) : Parcelable
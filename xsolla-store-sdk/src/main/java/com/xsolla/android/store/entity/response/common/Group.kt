package com.xsolla.android.store.entity.response.common

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Group(
    @SerializedName("external_id")
    val externalId: String? = null,
    val name: String? = null
) : Parcelable
package com.xsolla.android.store.entity.request.gamekeys

import com.google.gson.annotations.SerializedName

data class RedeemGameCodeBody(
    val code: String,
    @SerializedName("sandbox")
    val isSandbox: Boolean
)

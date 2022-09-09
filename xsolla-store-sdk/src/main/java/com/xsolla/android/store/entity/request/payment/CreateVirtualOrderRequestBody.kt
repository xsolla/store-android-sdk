package com.xsolla.android.store.entity.request.payment

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

internal data class CreateVirtualOrderRequestBody(
    @SerializedName("custom_parameters")
    val customParameters: JSONObject? = null
)

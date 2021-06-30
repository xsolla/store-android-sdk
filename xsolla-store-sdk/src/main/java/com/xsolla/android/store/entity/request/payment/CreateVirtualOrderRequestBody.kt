package com.xsolla.android.store.entity.request.payment

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import org.json.JSONObject

data class CreateVirtualOrderRequestBody(
    @SerializedName("custom_parameters")
    val customParameters: JSONObject? = null
)

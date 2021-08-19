package com.xsolla.android.store.entity.response.gamekeys

import com.google.gson.annotations.SerializedName

data class DrmListResponse(val drm: List<DrmItem> = emptyList())

data class DrmItem(
    val sku: String = "",
    val name: String? = null,
    val image: String? = null,
    val link: String? = null,
    @SerializedName("redeem_instruction_link")
    val redeemInstructionLink: String? = null,
    @SerializedName("drm_id")
    val drmId: Int = 0
)

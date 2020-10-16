package com.xsolla.android.store.entity.request.coupon

import com.google.gson.annotations.SerializedName

data class RedeemCouponRequestBody(
    @SerializedName("coupon_code")
    val couponCode: String,
    @SerializedName("selected_unit_items")
    val selectedUnitItems: Pair<String, String>? = null
)
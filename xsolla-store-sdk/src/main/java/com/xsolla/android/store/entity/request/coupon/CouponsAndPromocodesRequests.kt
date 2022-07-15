package com.xsolla.android.store.entity.request.coupon

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

internal data class RedeemCouponRequestBody(
    @SerializedName("coupon_code")
    val couponCode: String,
    @SerializedName("selected_unit_items")
    val selectedUnitItems: JsonObject? = null
)

internal data class RedeemPromocodeRequestBody(
    @SerializedName("coupon_code")
    val promocode: String,
    @SerializedName("selected_unit_items")
    val selectedUnitItems: JsonObject? = null,
    @SerializedName("cart")
    val cart: CartIdRequest = CartIdRequest("current")
)

internal data class RemovePromocodeRequestBody(
    @SerializedName("cart")
    val cart: CartIdRequest
)

internal data class CartIdRequest(val id: String)
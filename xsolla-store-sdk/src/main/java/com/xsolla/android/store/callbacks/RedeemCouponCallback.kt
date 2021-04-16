package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.items.RedeemCouponResponse

interface RedeemCouponCallback {
    fun onSuccess(response: RedeemCouponResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
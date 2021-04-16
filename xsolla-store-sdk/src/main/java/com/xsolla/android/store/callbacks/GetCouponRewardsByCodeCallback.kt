package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.items.RewardsByCodeResponse

interface GetCouponRewardsByCodeCallback {
    fun onSuccess(response: RewardsByCodeResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
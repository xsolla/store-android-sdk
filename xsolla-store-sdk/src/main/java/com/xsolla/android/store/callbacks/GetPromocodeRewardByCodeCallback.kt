package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.items.RewardsByPromocodeResponse

interface GetPromocodeRewardByCodeCallback {
    fun onSuccess(response: RewardsByPromocodeResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
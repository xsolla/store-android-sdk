package com.xsolla.android.store.callbacks.gamekeys

import com.xsolla.android.store.entity.response.gamekeys.DrmListResponse

interface GetDrmListCallback {
    fun onSuccess(response: DrmListResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
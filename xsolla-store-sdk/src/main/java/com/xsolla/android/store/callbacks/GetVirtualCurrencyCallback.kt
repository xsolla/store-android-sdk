package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.items.VirtualCurrencyPackageResponse
import com.xsolla.android.store.entity.response.items.VirtualCurrencyResponse

interface GetVirtualCurrencyCallback {
    fun onSuccess(response: VirtualCurrencyResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
interface GetVirtualCurrencyPackageCallback {
    fun onSuccess(response: VirtualCurrencyPackageResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
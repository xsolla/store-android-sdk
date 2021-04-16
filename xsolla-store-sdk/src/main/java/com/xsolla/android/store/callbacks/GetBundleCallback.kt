package com.xsolla.android.store.callbacks

import com.xsolla.android.store.entity.response.bundle.BundleItem
import com.xsolla.android.store.entity.response.bundle.BundleListResponse

interface GetBundleCallback {
    fun onSuccess(response: BundleItem)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
interface GetBundleListCallback {
    fun onSuccess(response: BundleListResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
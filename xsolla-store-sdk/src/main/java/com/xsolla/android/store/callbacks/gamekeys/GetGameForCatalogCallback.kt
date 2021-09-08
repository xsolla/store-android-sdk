package com.xsolla.android.store.callbacks.gamekeys

import com.xsolla.android.store.entity.response.gamekeys.GameItemsResponse

interface GetGameForCatalogCallback {
    fun onSuccess(response: GameItemsResponse.GameItem)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
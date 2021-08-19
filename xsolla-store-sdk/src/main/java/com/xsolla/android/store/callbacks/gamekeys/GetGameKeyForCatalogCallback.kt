package com.xsolla.android.store.callbacks.gamekeys

import com.xsolla.android.store.entity.response.gamekeys.GameKeysResponse

interface GetGameKeyForCatalogCallback {
    fun onSuccess(response: GameKeysResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
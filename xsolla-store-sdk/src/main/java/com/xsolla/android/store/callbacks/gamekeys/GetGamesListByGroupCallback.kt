package com.xsolla.android.store.callbacks.gamekeys

import com.xsolla.android.store.entity.response.gamekeys.GameItemsResponse

interface GetGamesListByGroupCallback {
    fun onSuccess(response: GameItemsResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
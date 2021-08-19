package com.xsolla.android.store.callbacks.gamekeys

import com.xsolla.android.store.entity.response.gamekeys.GamesOwnedResponse

interface GetListOfOwnedGamesCallback {
    fun onSuccess(response: GamesOwnedResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
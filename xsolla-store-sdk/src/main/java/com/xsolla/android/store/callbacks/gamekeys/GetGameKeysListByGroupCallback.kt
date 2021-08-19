package com.xsolla.android.store.callbacks.gamekeys

import com.xsolla.android.store.entity.response.gamekeys.GameKeysListByGroupResponse

interface GetGameKeysListByGroupCallback {
    fun onSuccess(response: GameKeysListByGroupResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
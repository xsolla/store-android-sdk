package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.SearchUsersByNicknameResponse

interface SearchUsersByNicknameCallback {
    fun onSuccess(data: SearchUsersByNicknameResponse)
    fun onError(throwable: Throwable?, errorMessage: String?)
}
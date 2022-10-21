package com.xsolla.android.login.callback

import com.xsolla.android.login.entity.response.SearchUsersByNicknameResponse

interface SearchUsersByNicknameCallback: BaseCallback {
    fun onSuccess(data: SearchUsersByNicknameResponse)
    override fun onError(throwable: Throwable?, errorMessage: String?)
}
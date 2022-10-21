package com.xsolla.android.login.callback

interface StartSocialCallback: BaseCallback {
    fun onAuthStarted()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}

interface StartSocialLinkingCallback: BaseCallback {
    fun onLinkingStarted()
    override fun onError(throwable: Throwable?, errorMessage: String?)
}
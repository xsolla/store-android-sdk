package com.xsolla.android.storesdkexample.listener

interface PurchaseListener {
    fun onSuccess()
    fun onFailure(errorMessage: String)
    fun showMessage(message: String)
}
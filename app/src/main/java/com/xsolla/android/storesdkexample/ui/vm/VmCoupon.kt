package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VmCoupon : ViewModel() {
    val operationResult = MutableLiveData<RedeemCouponResult>()
}

sealed class RedeemCouponResult {
    data class Success(val items: Any) : RedeemCouponResult()
    data class Failure(val message: String) : RedeemCouponResult()
}
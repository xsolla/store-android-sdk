package com.xsolla.android.storesdkexample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.store.XStore
import com.xsolla.android.store.api.XStoreCallback
import com.xsolla.android.store.entity.response.items.RedeemCouponResponse

class VmCoupon : ViewModel() {
    val operationResult = MutableLiveData<RedeemCouponResult>()

    fun redeemCoupon(coupon: String) {
        XStore.redeemCoupon(coupon, object : XStoreCallback<RedeemCouponResponse>() {
            override fun onSuccess(response: RedeemCouponResponse) {
                operationResult.value = RedeemCouponResult.Success(response.items)
            }

            override fun onFailure(errorMessage: String?) {
                operationResult.value = RedeemCouponResult.Failure(errorMessage ?: "Unknown error")
            }
        })
    }
}

sealed class RedeemCouponResult {
    data class Success(val items: Any) : RedeemCouponResult()
    data class Failure(val message: String) : RedeemCouponResult()
}
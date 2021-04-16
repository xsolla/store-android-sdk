package com.xsolla.android.inventorysdkexample.ui.vm

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.xsolla.android.store.XStore
import com.xsolla.android.store.callbacks.RedeemCouponCallback
import com.xsolla.android.store.entity.response.items.RedeemCouponResponse

class VmCoupon : ViewModel() {
    val operationResult = MutableLiveData<RedeemCouponResult>()

    fun redeemCoupon(coupon: String) {
        XStore.redeemCoupon(object : RedeemCouponCallback {
            override fun onSuccess(response: RedeemCouponResponse) {
                operationResult.value = RedeemCouponResult.Success(response.items)
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                operationResult.value = RedeemCouponResult.Failure(errorMessage ?: throwable?.javaClass?.name ?: "Unknown error")
            }

        }, coupon)
    }
}

sealed class RedeemCouponResult {
    data class Success(val items: List<RedeemCouponResponse.Item>) : RedeemCouponResult()
    data class Failure(val message: String) : RedeemCouponResult()
}
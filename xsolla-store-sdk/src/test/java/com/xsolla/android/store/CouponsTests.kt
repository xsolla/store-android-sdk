package com.xsolla.android.store

import com.xsolla.android.inventory.XInventory
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.store.callbacks.GetCouponRewardsByCodeCallback
import com.xsolla.android.store.callbacks.RedeemCouponCallback
import com.xsolla.android.store.entity.response.items.RedeemCouponResponse
import com.xsolla.android.store.entity.response.items.RewardsByCodeResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class CouponsTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun getCouponRewardsByCode_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: RewardsByCodeResponse? = null
        XStore.getCouponRewardsByCode(object : GetCouponRewardsByCodeCallback {
            override fun onSuccess(response: RewardsByCodeResponse) {
                res = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "xsWoLGJoMk")
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(res)
        Assert.assertNotNull(res!!.bonus.find {
            it.item.sku == couponItemSku
        })
    }

    @Test
    fun getCouponRewardsByCode_Fail() {
        var msg: String? = null
        var error = false
        val latch = CountDownLatch(1)
        var res: RewardsByCodeResponse? = null
        val unknownCode = "abc"
        XStore.getCouponRewardsByCode(object : GetCouponRewardsByCodeCallback {
            override fun onSuccess(response: RewardsByCodeResponse) {
                res = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                msg = errorMessage
                error = true
                latch.countDown()
            }
        }, unknownCode)
        latch.await()
        Assert.assertTrue(error)
        Assert.assertNull(res)
        Assert.assertEquals("[0401-9807]: Enter a valid coupon code.", msg)
    }

    @Test
    fun redeemCoupon_Fail() {
        var msg: String? = null
        var error = false
        val latch = CountDownLatch(1)
        var res: RedeemCouponResponse? = null
        val unknownCode = "abc"
        XStore.redeemCoupon(object : RedeemCouponCallback {
            override fun onSuccess(response: RedeemCouponResponse) {
                res = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                msg = errorMessage
                error = true
                latch.countDown()
            }
        }, unknownCode)
        latch.await()
        Assert.assertTrue(error)
        Assert.assertNull(res)
        Assert.assertEquals("[0401-9807]: Enter a valid coupon code.", msg)
    }

    @Test
    fun redeemCoupon_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: RedeemCouponResponse? = null
        XStore.redeemCoupon(object : RedeemCouponCallback {
            override fun onSuccess(response: RedeemCouponResponse) {
                res = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, validCoupon)
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(res)
        Assert.assertNotNull(res!!.items.find {
            it.sku == couponItemSku
        })
    }

    @Ignore("redeem is not always enough quick")
    @Test
    fun redeemCouponFlow_Success() {
        XInventory.init(projectId, userToken)

        var error = false
        var latch = CountDownLatch(1)
        var resInventoryBefore: InventoryResponse? = null
        XInventory.getInventory(object : GetInventoryCallback {
            override fun onSuccess(data: InventoryResponse) {
                resInventoryBefore = data
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(resInventoryBefore)
        val giftItemBefore = resInventoryBefore!!.items.find {
            it.sku == couponItemSku
        }
        Assert.assertNotNull(giftItemBefore)
        val quantityBefore = giftItemBefore!!.quantity!!

        error = false
        latch = CountDownLatch(1)
        var resRedeem: RedeemCouponResponse? = null
        XStore.redeemCoupon(object : RedeemCouponCallback {
            override fun onSuccess(response: RedeemCouponResponse) {
                resRedeem = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, validCoupon)
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(resRedeem)

        error = false
        latch = CountDownLatch(1)
        var resInventoryAfter: InventoryResponse? = null
        XInventory.getInventory(object : GetInventoryCallback {
            override fun onSuccess(data: InventoryResponse) {
                resInventoryAfter = data
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(resInventoryAfter)
        val giftItemAfter = resInventoryAfter!!.items.find {
            it.sku == couponItemSku
        }
        Assert.assertNotNull(giftItemAfter)
        val quantityAfter = giftItemAfter!!.quantity!!
        Assert.assertEquals(quantityBefore + 1, quantityAfter)
    }

}
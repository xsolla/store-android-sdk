package com.xsolla.android.store

import com.xsolla.android.store.callbacks.FillSpecificCartWithItemsCallback
import com.xsolla.android.store.callbacks.GetPromocodeRewardByCodeCallback
import com.xsolla.android.store.callbacks.RedeemPromocodeCallback
import com.xsolla.android.store.callbacks.RemovePromocodeCallback
import com.xsolla.android.store.entity.request.cart.FillCartItem
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.items.RewardsByPromocodeResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class PromocodeTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun getPromocodeRewardsByCode_Success() {
        var error = false
        val latch = CountDownLatch(1)
        var res: RewardsByPromocodeResponse? = null
        XStore.getPromocodeRewardsByCode(object : GetPromocodeRewardByCodeCallback {
            override fun onSuccess(response: RewardsByPromocodeResponse) {
                res = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, promocode)
        latch.await()
        Assert.assertFalse(error)
        Assert.assertEquals(promoItem, res?.bonus?.get(0)?.item?.sku)
        Assert.assertEquals(promoDiscount, res?.discount?.percent)
    }

    @Test
    fun getPromocodeRewardsByCode_Fail() {
        var error = false
        val latch = CountDownLatch(1)
        var msg: String? = null
        val badPromocode = promocode + promocode
        XStore.getPromocodeRewardsByCode(object : GetPromocodeRewardByCodeCallback {
            override fun onSuccess(response: RewardsByPromocodeResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                msg = errorMessage
                error = true
                latch.countDown()
            }
        }, badPromocode)
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("[0401-9807]: Enter a valid coupon code.", msg)
    }

    @Test
    fun redeemPromocode_Fail() {
        var error = false
        val latch = CountDownLatch(1)
        var msg: String? = null
        val badPromocode = promocode + promocode
        XStore.redeemPromocode(object : RedeemPromocodeCallback {
            override fun onSuccess(response: CartResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                msg = errorMessage
                error = true
                latch.countDown()
            }
        }, badPromocode)
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("[0401-9807]: Enter a valid coupon code.", msg)
    }

    @Test
    fun removePromocode_Fail() {
        var error = false
        val latch = CountDownLatch(1)
        var msg: String? = null
        XStore.removePromocode(object : RemovePromocodeCallback {
            override fun onSuccess(response: CartResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                msg = errorMessage
                error = true
                latch.countDown()
            }
        }, cartId = UUID.randomUUID().toString())
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("[0401-1401]: Cart for user user123 not found", msg)
    }

    @Test
    fun promocodeFlow_Success() {
        val cartId = UUID.randomUUID().toString()

        var latch = CountDownLatch(1)
        var error = false
        var cartResponse: CartResponse? = null
        XStore.fillCartByIdWithItems(object : FillSpecificCartWithItemsCallback {
            override fun onSuccess(response: CartResponse) {
                cartResponse = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, cartId, listOf(FillCartItem(itemForAddingToCart, 1)))
        latch.await()
        Assert.assertFalse(error)
        var price = cartResponse?.items?.get(0)?.price
        Assert.assertNotNull(price)
        Assert.assertTrue(price!!.getAmountRaw()!! == price.getAmountWithoutDiscountRaw())
        Assert.assertNull(cartResponse?.items?.map { it.sku }?.find { it == promoItem })

        latch = CountDownLatch(1)
        error = false
        cartResponse = null
        XStore.redeemPromocode(object : RedeemPromocodeCallback {
            override fun onSuccess(response: CartResponse) {
                cartResponse = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, promocode, null, cartId)
        latch.await()
        Assert.assertFalse(error)
        price = cartResponse?.items?.get(0)?.price
        Assert.assertNotNull(price)
        Assert.assertTrue(price!!.getAmountDecimal()!! < price.getAmountWithoutDiscountDecimal())
        Assert.assertNotNull(cartResponse?.items?.map { it.sku }?.find { it == promoItem })

        latch = CountDownLatch(1)
        error = false
        cartResponse = null
        XStore.removePromocode(object : RemovePromocodeCallback {
            override fun onSuccess(response: CartResponse) {
                cartResponse = response
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, cartId)
        latch.await()
        Assert.assertFalse(error)
        price = cartResponse?.items?.get(0)?.price
        Assert.assertNotNull(price)
        Assert.assertTrue(price!!.getAmountDecimal()!! == price.getAmountWithoutDiscountDecimal())
        Assert.assertNull(cartResponse?.items?.map { it.sku }?.find { it == promoItem })
    }

}
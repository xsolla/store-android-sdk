package com.xsolla.android.store

import com.xsolla.android.store.callbacks.*
import com.xsolla.android.store.entity.request.cart.FillCartItem
import com.xsolla.android.store.entity.response.cart.CartResponse
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class CartTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun cartFlow_Success() {
        // Clear cart
        var error = false
        var latch = CountDownLatch(1)
        XStore.clearCurrentCart(object : ClearCurrentCartCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        // Check cart is empty
        error = false
        latch = CountDownLatch(1)
        var items: List<CartResponse.Item>? = null
        XStore.getCurrentCart(object : GetCurrentUserCartCallback {
            override fun onSuccess(response: CartResponse) {
                items = response.items
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertTrue(items?.isEmpty() ?: false)
        // Add some item
        error = false
        latch = CountDownLatch(1)
        XStore.updateItemFromCurrentCart(object : UpdateItemFromCurrentCartCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, itemForCart, 3)
        latch.await()
        Assert.assertFalse(error)
        // Get cart with added item
        error = false
        latch = CountDownLatch(1)
        items = null
        XStore.getCurrentCart(object : GetCurrentUserCartCallback {
            override fun onSuccess(response: CartResponse) {
                items = response.items
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(items)
        Assert.assertEquals(itemForCart, items!![0].sku)
        Assert.assertEquals(3, items!![0].quantity)
        // Delete item
        error = false
        latch = CountDownLatch(1)
        XStore.deleteItemFromCurrentCart(object : DeleteItemFromCurrentCartCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, itemForCart)
        latch.await()
        Assert.assertFalse(error)
        // Check cart is empty
        error = false
        latch = CountDownLatch(1)
        items = null
        XStore.getCurrentCart(object : GetCurrentUserCartCallback {
            override fun onSuccess(response: CartResponse) {
                items = response.items
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertTrue(items?.isEmpty() ?: false)
        // Fill cart
        error = false
        latch = CountDownLatch(1)
        XStore.fillCurrentCartWithItems(object : FillCartWithItemsCallback {
            override fun onSuccess(response: CartResponse) {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, listOf(FillCartItem(itemForCart, 10)))
        latch.await()
        Assert.assertFalse(error)
        // Get filled cart
        error = false
        latch = CountDownLatch(1)
        items = null
        XStore.getCurrentCart(object : GetCurrentUserCartCallback {
            override fun onSuccess(response: CartResponse) {
                items = response.items
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(items)
        Assert.assertEquals(itemForCart, items!![0].sku)
        Assert.assertEquals(10, items!![0].quantity)
        // Create order from cart
        error = false
        latch = CountDownLatch(1)
        var orderId: Int? = null
        var token: String? = null
        XStore.createOrderFromCurrentCart(object : CreateOrderCallback {
            override fun onSuccess(response: CreateOrderResponse) {
                orderId = response.orderId
                token = response.token
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertNotNull(orderId)
        Assert.assertNotEquals(0, orderId)
        Assert.assertNotNull(token)
    }

}
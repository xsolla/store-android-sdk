package com.xsolla.android.inventory

import com.xsolla.android.inventory.callback.ConsumeItemCallback
import com.xsolla.android.inventory.callback.GetInventoryCallback
import com.xsolla.android.inventory.callback.GetSubscriptionsCallback
import com.xsolla.android.inventory.callback.GetVirtualBalanceCallback
import com.xsolla.android.inventory.entity.response.InventoryResponse
import com.xsolla.android.inventory.entity.response.SubscriptionsResponse
import com.xsolla.android.inventory.entity.response.VirtualBalanceResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class InventoryTests {

    @Before
    fun initSdk() {
        XInventory.init(projectId, userToken)
    }

    @Test
    fun getInventory() {
        var success = false
        val latch = CountDownLatch(1)
        XInventory.getInventory(object : GetInventoryCallback {
            override fun onSuccess(data: InventoryResponse) {
                success = data.items.isEmpty()
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                latch.countDown()
            }
        }, 25, 0)
        latch.await()
        Assert.assertEquals(true, success)
    }

    @Test
    fun getVirtualBalance() {
        var success = false
        val latch = CountDownLatch(1)
        XInventory.getVirtualBalance(object : GetVirtualBalanceCallback {
            override fun onSuccess(data: VirtualBalanceResponse) {
                success = data.items.isNotEmpty()
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertEquals(true, success)
    }

    @Test
    fun getSubscriptions() {
        var success = false
        val latch = CountDownLatch(1)
        XInventory.getSubscriptions(object : GetSubscriptionsCallback {
            override fun onSuccess(data: SubscriptionsResponse) {
                success = true
                success = success && data.items.size == 1
                success = success && data.items[0].sku == subscriptionSku
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertEquals(true, success)
    }

    @Test
    fun consumeItem() {
        var success = false
        val latch = CountDownLatch(1)
        XInventory.consumeItem(itemForConsume, 3, null, object : ConsumeItemCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                success = errorMessage == "[0401-1102]: Unprocessable Entity"
                latch.countDown()
            }

        })
        latch.await()
        Assert.assertEquals(true, success)
    }

}
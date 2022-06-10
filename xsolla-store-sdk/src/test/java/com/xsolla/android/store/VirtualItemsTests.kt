package com.xsolla.android.store

import com.xsolla.android.store.callbacks.GetVirtualItemsCallback
import com.xsolla.android.store.callbacks.GetVirtualItemsShortCallback
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse
import com.xsolla.android.store.entity.response.items.VirtualItemsShortResponse
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class VirtualItemsTests {

    @Before
    fun initSdk() {
        XStore.init(projectId, userToken)
    }

    @Test
    fun getVirtualItems_Success() {
        var error = false
        var found = true
        val latch = CountDownLatch(1)
        XStore.getVirtualItems(object : GetVirtualItemsCallback {
            override fun onSuccess(response: VirtualItemsResponse) {
                itemsInCatalog.forEach { item ->
                    found = found && (response.items.map { it.sku }.find { it == item } != null)
                }
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertTrue(found)
    }

    @Test
    fun getVirtualItemsShort_Success() {
        var error = false
        var found = true
        val latch = CountDownLatch(1)
        XStore.getVirtualItemsShort(object : GetVirtualItemsShortCallback {
            override fun onSuccess(response: VirtualItemsShortResponse) {
                itemsInCatalog.forEach { item ->
                    found = found && (response.items.map { it.sku }.find { it == item } != null)
                }
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertTrue(found)
    }

}
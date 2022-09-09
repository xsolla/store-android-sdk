package com.xsolla.android.store

import com.xsolla.android.store.callbacks.GetVirtualItemsShortCallback
import com.xsolla.android.store.entity.response.items.VirtualItemsShortResponse
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class UnauthenticatedFlowTests {

    @Test
    fun lateAuthFlowTest_Success() {
        XStore.init(projectId)
        var error = false
        var unAuthSize = 0
        var latch = CountDownLatch(1)
        XStore.getVirtualItemsShort(object : GetVirtualItemsShortCallback {
            override fun onSuccess(response: VirtualItemsShortResponse) {
                unAuthSize = response.items.size
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        XStore.setAuthToken(userToken)
        error = false
        var authSize = 0
        latch = CountDownLatch(1)
        XStore.getVirtualItemsShort(object : GetVirtualItemsShortCallback {
            override fun onSuccess(response: VirtualItemsShortResponse) {
                authSize = response.items.size
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertTrue(authSize >= unAuthSize)
    }

}
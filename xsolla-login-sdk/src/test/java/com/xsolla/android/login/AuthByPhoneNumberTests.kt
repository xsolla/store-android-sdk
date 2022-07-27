package com.xsolla.android.login

import com.xsolla.android.login.callback.CompletePasswordlessAuthCallback
import com.xsolla.android.login.callback.StartPasswordlessAuthCallback
import com.xsolla.android.login.entity.response.StartPasswordlessAuthResponse
import com.xsolla.android.login.util.TestUtils.initLoggedOut
import com.xsolla.android.login.util.TestUtils.initSdkOauth
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class AuthByPhoneNumberTests {

    @Ignore("for manual testing (because of limits)")
    @Test
    fun startPhoneNumberAuthOauth_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        var operationId: String? = null
        XLogin.startAuthByMobilePhone(phoneNumber, object : StartPasswordlessAuthCallback {
            override fun onAuthStarted(data: StartPasswordlessAuthResponse) {
                operationId = data.operationId
                println("!!! oauth operation id = $operationId")
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertFalse(error)
        Assert.assertFalse(operationId.isNullOrEmpty())
    }

    @Test
    fun startPhoneNumberAuthOauth_Fail() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        var err: String? = null
        XLogin.startAuthByMobilePhone(
            phoneNumber + phoneNumber,
            object : StartPasswordlessAuthCallback {
                override fun onAuthStarted(data: StartPasswordlessAuthResponse) {
                    latch.countDown()
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    error = true
                    err = errorMessage
                    latch.countDown()
                }
            })
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("body.phone_number in body should match '^\\+(\\d){5,25}\$'", err)
    }

    @Test
    fun completeAuthByMobilePhoneOauth_Fail() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        var err: String? = null
        XLogin.completeAuthByMobilePhone(
            phoneNumber,
            smsCode,
            operationId,
            object : CompletePasswordlessAuthCallback {
                override fun onSuccess() {
                    latch.countDown()
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    error = true
                    err = errorMessage
                    latch.countDown()
                }
            })
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("Wrong authorization code.", err)
    }

    @Ignore("for manual testing (needs correct sms code and phone number substitution)")
    @Test
    fun completeAuthByMobilePhoneOauth_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.completeAuthByMobilePhone(
            phoneNumber,
            smsCode,
            operationId,
            object : CompletePasswordlessAuthCallback {
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
        Assert.assertFalse(XLogin.token.isNullOrEmpty())
        Assert.assertTrue(XLogin.canRefreshToken())
        Assert.assertFalse(XLogin.isTokenExpired())
    }

}
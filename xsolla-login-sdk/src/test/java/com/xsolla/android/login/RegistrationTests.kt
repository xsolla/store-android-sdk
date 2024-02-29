package com.xsolla.android.login

import com.xsolla.android.login.callback.RegisterCallback
import com.xsolla.android.login.callback.ResendAccountConfirmationEmailCallback
import com.xsolla.android.login.util.TestUtils.initLoggedOut
import com.xsolla.android.login.util.TestUtils.initSdkOauth
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class RegistrationTests {

    //TODO check acceptConsent, promoEmailAgreement and fail scenarios
    @Test
    fun registerOauth_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.register(
            UUID.randomUUID().toString(),
            "${UUID.randomUUID()}@gmail.com",
            UUID.randomUUID().toString(),
            object : RegisterCallback {
                override fun onSuccess() {
                    latch.countDown()
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    error = true
                    latch.countDown()
                }
            }
        )
        latch.await()
        Assert.assertFalse(error)
    }

    @Test
    fun registerOauthWithLocale_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.register(
            UUID.randomUUID().toString(),
            "${UUID.randomUUID()}@gmail.com",
            UUID.randomUUID().toString(),
            object : RegisterCallback {
                override fun onSuccess() {
                    latch.countDown()
                }

                override fun onError(throwable: Throwable?, errorMessage: String?) {
                    error = true
                    latch.countDown()
                }
            },
            locale = "zh_CN"
        )
        latch.await()
        Assert.assertFalse(error)
    }

    @Test
    @Ignore("api was changed, need to rework this test")
    fun resendAccountConfirmationEmail_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.resendAccountConfirmationEmail(username, object : ResendAccountConfirmationEmailCallback {
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
    }

    @Test
    @Ignore("api was changed, need to rework this test")
    fun resendAccountConfirmationEmailWithLocale_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.resendAccountConfirmationEmail(username, object : ResendAccountConfirmationEmailCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, locale = "zh_CN")
        latch.await()
        Assert.assertFalse(error)
    }

}
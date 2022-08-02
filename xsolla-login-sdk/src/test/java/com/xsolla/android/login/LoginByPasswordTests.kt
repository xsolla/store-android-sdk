package com.xsolla.android.login

import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.login.callback.ResetPasswordCallback
import com.xsolla.android.login.util.TestUtils.initLoggedInByPassword
import com.xsolla.android.login.util.TestUtils.initLoggedOut
import com.xsolla.android.login.util.TestUtils.initSdkOauth
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.CountDownLatch

@RunWith(RobolectricTestRunner::class)
class LoginByPasswordTests {

    @Test
    fun loginOauth_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.login(username, password, object : AuthCallback {
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
        Assert.assertNotNull(XLogin.token)
    }

    @Test
    fun loginOauth_Fail() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        var msg: String? = null
        XLogin.login(username, password + password, object : AuthCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                msg = errorMessage
                latch.countDown()
            }
        })
        latch.await()
        Assert.assertTrue(error)
        Assert.assertEquals("Wrong username or password.", msg)
    }

    @Test
    fun logoutOauth_Success() {
        initSdkOauth()
        initLoggedInByPassword()

        Assert.assertNotNull(XLogin.token)
        XLogin.logout()
        Assert.assertNull(XLogin.token)
    }

    @Test
    fun resetPassword_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.resetPassword(username, object : ResetPasswordCallback {
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
    fun resetPasswordWithLocale_Success() {
        initSdkOauth()
        initLoggedOut()

        val latch = CountDownLatch(1)
        var error = false
        XLogin.resetPassword(username, object : ResetPasswordCallback {
            override fun onSuccess() {
                latch.countDown()
            }

            override fun onError(throwable: Throwable?, errorMessage: String?) {
                error = true
                latch.countDown()
            }
        }, "zh_CN")
        latch.await()
        Assert.assertFalse(error)
    }

}
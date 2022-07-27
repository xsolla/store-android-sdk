package com.xsolla.android.login.util

import android.content.Context
import android.provider.Settings
import androidx.test.core.app.ApplicationProvider
import com.xsolla.android.login.*
import com.xsolla.android.login.callback.AuthCallback
import com.xsolla.android.login.callback.AuthViaDeviceIdCallback
import org.junit.Assert
import java.util.concurrent.CountDownLatch

object TestUtils {

    // Init methods for different auth types

    fun initSdkOauth() {
        val loginConfig = LoginConfig.OauthBuilder()
            .setProjectId(projectId)
            .setOauthClientId(oauthClientId)
            .setRedirectUriScheme("app")
            .setRedirectUriHost("example")
            .build()
        XLogin.init(ApplicationProvider.getApplicationContext(), loginConfig)
    }

    // Init for different auth states

    fun initLoggedOut() {
        XLogin.logout()
    }

    fun initLoggedInByDeviceId() {
        XLogin.logout()
        val context: Context = ApplicationProvider.getApplicationContext()
        Settings.Secure.putString(context.contentResolver, Settings.Secure.ANDROID_ID, deviceId)
        val latch = CountDownLatch(1)
        var error = false
        XLogin.authenticateViaDeviceId(object : AuthViaDeviceIdCallback {
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

    fun initLoggedInByPassword() {
        XLogin.logout()
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
    }

}
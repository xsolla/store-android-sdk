package com.xsolla.android.storesdkexample

import android.app.Application
import android.content.Context
import com.xsolla.android.appcore.utils.AmountUtils
import com.xsolla.android.login.LoginConfig
import com.xsolla.android.login.XLogin
import com.xsolla.android.storesdkexample.data.local.DemoCredentialsManager

class App : Application() {

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        fun app(): App {
            return instance!!
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        AmountUtils.init(applicationContext())
        initLogin()
    }

    fun initLogin() {
        val socialConfig = XLogin.SocialConfig(
            googleServerId = BuildConfig.GOOGLE_CREDENTIAL
        )

        val loginConfig = LoginConfig.OauthBuilder()
            .setProjectId(DemoCredentialsManager.loginId)
            .setOauthClientId(DemoCredentialsManager.oauthClientId)
            .setSocialConfig(socialConfig)
            .build()
        XLogin.init(this, loginConfig)
    }

}
package com.xsolla.android.inventorysdkexample

import android.app.Application
import android.content.Context
import com.xsolla.android.login.LoginConfig
import com.xsolla.android.login.XLogin

class App : Application() {

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }

    override fun onCreate() {
        super.onCreate()
        val loginConfig = if (BuildConfig.USE_OAUTH) {
            LoginConfig.OauthBuilder()
                .setProjectId(BuildConfig.LOGIN_ID)
                .setOauthClientId(BuildConfig.OAUTH_CLIENT_ID)
                .build()
        } else {
            LoginConfig.JwtBuilder()
                .setProjectId(BuildConfig.LOGIN_ID)
                .build()
        }
        XLogin.init(this, loginConfig)
    }
}
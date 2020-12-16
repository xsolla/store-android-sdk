package com.xsolla.android.storesdkexample

import android.app.Application
import android.content.Context
import com.xsolla.android.login.LoginConfig
import com.xsolla.android.login.XLogin

class App: Application() {

    companion object {
        private var instance: App? = null

        fun applicationContext(): Context {
            return instance!!.applicationContext
        }
    }

    init {
        instance = this
    }

    // 1. Собирается и нативная и не нативная (flavors)
    // 2. google server id не заливается

    // Взять образ другой, docker hub
    override fun onCreate() {
        super.onCreate()

        val credential = BuildConfig.GOOGLE_CREDENTIAL
        if (BuildConfig.USE_OAUTH) {
            val loginConfig = LoginConfig.OauthBuilder()
                .setProjectId(BuildConfig.LOGIN_ID)
                .setOauthClientId(BuildConfig.OAUTH_CLIENT_ID)
                .build()

            XLogin.init(this, loginConfig)
        } else {
            val loginConfig = LoginConfig.JwtBuilder()
                .setProjectId(BuildConfig.LOGIN_ID)
                .setSocialConfig(XLogin.SocialConfig(googleServerId = "653881162314-g6cnf0n2m62ll90ee571uqg6h6hors8q.apps.googleusercontent.com"))
                .build()

            XLogin.init(this, loginConfig)
        }
    }
}
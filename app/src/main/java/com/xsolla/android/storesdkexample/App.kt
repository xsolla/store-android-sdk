package com.xsolla.android.storesdkexample

import android.app.Application
import android.content.Context
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

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.USE_OAUTH) {
            XLogin.initOauth(this, BuildConfig.LOGIN_ID, BuildConfig.OAUTH_CLIENT_ID)
        } else {
            XLogin.initJwt(this, BuildConfig.LOGIN_ID)
        }
    }
}
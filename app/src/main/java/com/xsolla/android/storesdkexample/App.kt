package com.xsolla.android.storesdkexample

import android.app.Application
import com.xsolla.android.login.XLogin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        XLogin.init(this, BuildConfig.LOGIN_ID, BuildConfig.USE_OAUTH, null)
    }
}
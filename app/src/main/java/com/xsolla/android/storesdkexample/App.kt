package com.xsolla.android.storesdkexample

import android.app.Application
import com.xsolla.android.login.XLogin

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        XLogin.init(BuildConfig.LOGIN_ID, this, null)
    }
}
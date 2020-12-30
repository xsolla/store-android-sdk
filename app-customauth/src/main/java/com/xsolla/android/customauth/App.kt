package com.xsolla.android.customauth

import android.app.Application
import android.content.Context

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
}
package com.xsolla.android.login.ui

import androidx.appcompat.app.AppCompatActivity

abstract class ActivityAuth : AppCompatActivity() {
    companion object {
        const val ARG_AUTH_URL = "auth_url"
        const val ARG_CALLBACK_URL = "callback_url"

        const val RESULT = "result"
    }
}
package com.xsolla.android.login.ui

import androidx.appcompat.app.AppCompatActivity

abstract class ActivityAuth : AppCompatActivity() {
    companion object {
        const val ARG_URL = "token"

        const val RESULT = "result"
    }
}
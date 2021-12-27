package com.xsolla.android.payments.ui

import androidx.appcompat.app.AppCompatActivity

abstract class ActivityPaystation : AppCompatActivity() {
    companion object {
        const val ARG_URL = "url"
        const val ARG_REDIRECT_SCHEME = "redirect_scheme"
        const val ARG_REDIRECT_HOST = "redirect_host"

        const val RESULT = "result"
    }
}
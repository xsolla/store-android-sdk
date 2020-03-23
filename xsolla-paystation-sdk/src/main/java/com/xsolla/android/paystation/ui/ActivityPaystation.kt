package com.xsolla.android.paystation.ui

import androidx.appcompat.app.AppCompatActivity

abstract class ActivityPaystation : AppCompatActivity() {
    companion object {
        const val ARG_URL = "token"

        const val RESULT = "result"
    }
}
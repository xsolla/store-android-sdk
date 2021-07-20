package com.xsolla.android.login.ui

import android.content.Intent
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.parcelize.Parcelize

abstract class ActivityAuth : AppCompatActivity() {
    companion object {
        const val ARG_AUTH_URL = "auth_url"
        const val ARG_CALLBACK_URL = "callback_url"

        const val RESULT = "result"
    }

    @Parcelize
    data class Result(
        val status: Status,
        val token: String?,
        val code: String?,
        val error: String?
    ) : Parcelable {
        companion object {
            @JvmStatic
            fun fromResultIntent(intent: Intent?): Result =
                intent?.getParcelableExtra(ActivityAuthWebView.RESULT)
                    ?: Result(Status.ERROR, null, null, "Unknown")
        }
    }

    enum class Status {
        SUCCESS,
        CANCELLED,
        ERROR
    }
}
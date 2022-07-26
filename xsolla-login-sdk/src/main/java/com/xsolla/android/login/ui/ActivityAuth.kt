package com.xsolla.android.login.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import kotlinx.parcelize.Parcelize

internal abstract class ActivityAuth : AppCompatActivity() {
    companion object {
        const val ARG_AUTH_URL = "auth_url"
        const val ARG_CALLBACK_URL = "callback_url"
        const val ARG_IS_LINKING = "is_linking"
        const val ARG_SOCIAL_NETWORK = "social"

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
                intent?.getParcelableExtra(RESULT)
                    ?: Result(Status.ERROR, null, null, "Unknown")
        }
    }

    enum class Status {
        SUCCESS,
        CANCELLED,
        ERROR
    }

    protected fun handleCallbackUrlRedirect(uri: Uri) {
        val token = uri.getQueryParameter("token")
        val code = uri.getQueryParameter("code")
        val errorCode = uri.getQueryParameter("error_code")
        val errorDescription = uri.getQueryParameter("error_description")
        val isLinking = intent.getBooleanExtra(ARG_IS_LINKING, false)
        if (isLinking) {
            if (errorCode != null) {
                finishWithResult(
                    Activity.RESULT_OK,
                    Result(Status.ERROR, null, null, errorDescription ?: "Error: $errorCode")
                )
            } else {
                finishWithResult(
                    Activity.RESULT_OK,
                    Result(Status.SUCCESS, null, null, null)
                )
            }
        } else {
            if (code == null && token == null) {
                finishWithResult(
                    Activity.RESULT_OK,
                    Result(Status.ERROR, null, null, errorDescription ?: "Error: $errorCode")
                )
            } else {
                finishWithResult(
                    Activity.RESULT_OK,
                    Result(Status.SUCCESS, token, code, null)
                )
            }
        }
    }

    protected fun finishWithResult(resultCode: Int, resultData: Result) {
        val intent = Intent()
        intent.putExtra(RESULT, resultData)
        setResult(resultCode, intent)
        finish()
    }
}
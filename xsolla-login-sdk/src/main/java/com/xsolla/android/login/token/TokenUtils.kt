package com.xsolla.android.login.token

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri

internal class TokenUtils(context: Context) {

    private val preferences: SharedPreferences = context.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

    var oauthAccessToken: String?
        get() = preferences.getString("oauthAccessToken", null)
        set(value) = preferences
                .edit()
                .putString("oauthAccessToken", value)
                .apply()

    var oauthRefreshToken: String?
        get() = preferences.getString("oauthRefreshToken", null)
        set(value) = preferences
                .edit()
                .putString("oauthRefreshToken", value)
                .apply()

    var oauthExpireTimeUnixSec: Long
        get() = preferences.getLong("oauthExpire", 0)
        set(value) = preferences
                .edit()
                .putLong("oauthExpire", value)
                .apply()

    companion object {

        private const val PREFS_FILE_NAME = "XSOLLA_LOGIN"

        fun getTokenFromUrl(url: String): String? {
            val uri = Uri.parse(url)
            return uri.getQueryParameter("token")
        }

        fun getCodeFromUrl(url: String): String? {
            val uri = Uri.parse(url)
            return uri.getQueryParameter("code")
        }

    }

}
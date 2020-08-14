package com.xsolla.android.login.token

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.xsolla.android.login.jwt.JWT

class TokenUtils(context: Context) {

    private val preferences: SharedPreferences = context.applicationContext.getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE)

    val jwt: JWT?
        get() {
            val token = jwtToken ?: return null
            return JWT(token)
        }

    var jwtToken: String?
        get() = preferences.getString("jwtToken", null)
        set(value) = preferences
                .edit()
                .putString("jwtToken", value)
                .apply()

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
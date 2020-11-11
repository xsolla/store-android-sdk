package com.xsolla.android.customauth.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.xsolla.android.customauth.App

object PrefManager {
    private const val TOKEN_KEY = "access_token_prefs"
    private const val EMAIL_KEY = "email_prefs"

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    fun setToken(token: String) = preferences.edit { putString(TOKEN_KEY, token) }
    val token: String?
        get() = preferences.getString(TOKEN_KEY, null)

    fun setEmail(email: String) = preferences.edit { putString(EMAIL_KEY, email) }
    val email: String?
        get() = preferences.getString(EMAIL_KEY, null)

    fun clearAll() = preferences.edit { clear() }
}
package com.xsolla.android.customauth.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.xsolla.android.customauth.App

object PrefManager {
    private const val TOKEN_KEY = "access_token"

    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    fun setToken(token: String) = preferences.edit { putString("token", token) }
    fun getToken(): String? = preferences.getString("token", null)
}
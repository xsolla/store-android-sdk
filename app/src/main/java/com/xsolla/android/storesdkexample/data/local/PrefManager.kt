package com.xsolla.android.storesdkexample.data.local

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.preference.PreferenceManager
import com.xsolla.android.storesdkexample.App

object PrefManager {
    private val preferences: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(App.applicationContext())
    }

    fun setUserLevel(userId: String, level: Int) = preferences.edit { putInt("userLevel_$userId", level) }
    fun getUserLevel(userId: String) = preferences.getInt("userLevel_$userId", 1)

    fun setAvatar(userId: String, resource: Int) = preferences.edit { putInt("avatar_$userId", resource) }
    fun getAvatar(userId: String) = preferences.getInt("avatar_$userId", -1)
}
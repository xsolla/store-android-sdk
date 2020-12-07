package com.xsolla.android.customauth.data.remote.dto

import com.google.gson.annotations.SerializedName

data class AuthRequest(val email: String)

data class AuthResponse(@SerializedName("access_token") val accessToken: String)
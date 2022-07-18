package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

internal data class AuthUserBody(
    val username: String,
    val password: String,
    @SerializedName("remember_me")
    val rememberMe: Boolean
) {
    constructor(username: String, password: String): this(username, password, false)
}
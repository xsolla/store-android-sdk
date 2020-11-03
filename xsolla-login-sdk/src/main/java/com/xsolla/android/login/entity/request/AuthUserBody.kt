package com.xsolla.android.login.entity.request

data class AuthUserBody(
    val username: String,
    val password: String,
    val remember_me: Boolean
) {
    constructor(username: String, password: String): this(username, password, false)
}
package com.xsolla.android.login.entity.request

data class RegisterUserBody(
    val username: String,
    val email: String,
    val password: String,
)
package com.xsolla.android.login.entity.request

data class OauthRegisterUserBody(
        val username: String,
        val email: String,
        val password: String
)
package com.xsolla.lib_login.util

import com.xsolla.lib_login.entity.response.Error

internal class LoginApiException(
    val error: Error,
    cause: Throwable
) : Exception(cause)
package com.xsolla.lib_login.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CreateCodeForAccountsLinkingResponse(
    @SerialName("code")
    val code: String
)
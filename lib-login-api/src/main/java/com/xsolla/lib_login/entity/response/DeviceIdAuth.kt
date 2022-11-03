package com.xsolla.lib_login.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LinkEmailPasswordResponse(
    @SerialName("email_confirmation_required")
    val emailConfirmationRequired: Boolean
)
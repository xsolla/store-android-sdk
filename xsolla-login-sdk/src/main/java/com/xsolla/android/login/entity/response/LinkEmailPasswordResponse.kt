package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class LinkEmailPasswordResponse(
    @SerializedName("email_confirmation_required")
    val emailConfirmationRequired: Boolean
)
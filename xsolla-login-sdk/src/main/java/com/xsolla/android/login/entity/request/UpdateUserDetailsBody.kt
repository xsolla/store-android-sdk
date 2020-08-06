package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

data class UpdateUserDetailsBody(
        val birthday: String?,
        @SerializedName("first_name")
        val firstName: String?,
        val gender: String?,
        @SerializedName("last_name")
        val lastName: String?,
        val nickname: String?
)
package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

internal data class CheckUserAgeBody(
    @SerializedName("project_id")
    val projectId: String,
    @SerializedName("dob")
    val birthday: String
)
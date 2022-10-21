package com.xsolla.lib_login.entity.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CheckUserAgeBody(
    @SerialName("project_id")
    val projectId: String,
    @SerialName("dob")
    val birthday: String
)

@Serializable
internal data class UpdateUserDetailsBody(
    @SerialName("birthday")
    val birthday: String?,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("gender")
    val gender: String?,
    @SerialName("last_name")
    val lastName: String?,
    @SerialName("nickname")
    val nickname: String?
)

@Serializable
internal data class UpdateUserPhoneBody(
    @SerialName("phone_number")
    val phoneNumber: String
)
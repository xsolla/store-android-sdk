package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class UserDetailsResponse(
    val ban: BanInfo?,
    val birthday: String?,
    @SerializedName("connection_information")
    val connectionInformation: String?,
    val country: String?,
    val email: String?,
    @SerializedName("external_id")
    val externalId: String?,
    @SerializedName("first_name")
    val first_name: String,
    val gender: String?,
    val groups: List<GroupInfo>?,
    val id: String,
    @SerializedName("last_login")
    val lastLoginTime: String,
    @SerializedName("last_name")
    val lastName: String?,
    val name: String?,
    val nickname: String?,
    val phone: String?,
    val picture: String?,
    @SerializedName("registered")
    val registrationTime: String
)

data class BanInfo(
        @SerializedName("date_from")
        val dateFrom: String,
        @SerializedName("date_to")
        val dateTo: String,
        val reason: String?
)

data class GroupInfo(
        val id: Int,
        @SerializedName("is_default")
        val is_default: Boolean,
        @SerializedName("is_deletable")
        val is_deletable: Boolean,
        val name: String?
)
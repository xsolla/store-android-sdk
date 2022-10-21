package com.xsolla.lib_login.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class CheckUserAgeResponse(
    @SerialName("accepted")
    val accepted: Boolean
)

@Serializable
internal data class UserDetailsResponse(
    @SerialName("ban")
    val ban: BanInfo?,
    @SerialName("birthday")
    val birthday: String?,
    @SerialName("connection_information")
    val connectionInformation: String?, // For Korean users only
    @SerialName("country")
    val country: String?,
    @SerialName("email")
    val email: String?,
    @SerialName("username")
    val username: String?,
    @SerialName("external_id")
    val externalId: String?,
    @SerialName("first_name")
    val firstName: String?,
    @SerialName("gender")
    val gender: GenderResponse?,
    @SerialName("groups")
    val groups: List<GroupInfo>?,
    @SerialName("id")
    val id: String,
    @SerialName("last_login")
    val lastLoginTime: String?,
    @SerialName("last_name")
    val lastName: String?,
    @SerialName("name")
    val name: String?,
    @SerialName("nickname")
    val nickname: String?,
    @SerialName("phone")
    val phone: String?,
    @SerialName("picture")
    val picture: String?,
    @SerialName("registered")
    val registrationTime: String?,
    @SerialName("is_anonymous")
    val isAnonymous: Boolean?,
    @SerialName("tag")
    val tag: String?
)

@Serializable
internal data class BanInfo(
    @SerialName("date_from")
    val dateFrom: String,
    @SerialName("date_to")
    val dateTo: String?,
    @SerialName("reason")
    val reason: String?
)

@Serializable
internal data class GroupInfo(
    @SerialName("id")
    val id: Int,
    @SerialName("is_default")
    val is_default: Boolean,
    @SerialName("is_deletable")
    val is_deletable: Boolean,
    @SerialName("name")
    val name: String
)

@Serializable
internal enum class GenderResponse {
    @SerialName("f")
    F,

    @SerialName("m")
    M,

    @SerialName("other")
    OTHER,

    @SerialName("prefer not to answer")
    PREFER_NOT_TO_ANSWER
}

@Serializable
internal data class EmailResponse(
    @SerialName("current_email")
    val email: String?
)

@Serializable
internal data class PhoneResponse(
    @SerialName("phone")
    val phone: String?
)

@Serializable
internal data class PictureResponse(
    @SerialName("picture")
    val picture: String
)
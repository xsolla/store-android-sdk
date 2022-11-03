package com.xsolla.android.login.entity.response

data class UserDetailsResponse(
    val ban: BanInfo?,
    val birthday: String?,
    val connectionInformation: String?, // For Korean users only
    val country: String?,
    val email: String?,
    val username: String?,
    val externalId: String?,
    val firstName: String?,
    val gender: GenderResponse?,
    val groups: List<GroupInfo> = emptyList(),
    val id: String,
    val lastLoginTime: String,
    val lastName: String?,
    val name: String?,
    val nickname: String?,
    val phone: String?,
    val picture: String?,
    val registrationTime: String,
    val isAnonymous: Boolean?,
    val tag: String?
)

internal fun fromLibUserDetails(details: com.xsolla.lib_login.entity.response.UserDetailsResponse): UserDetailsResponse =
    UserDetailsResponse(
        ban = fromLibBanInfo(details.ban),
        birthday = details.birthday,
        connectionInformation = details.connectionInformation,
        country = details.country,
        email = details.email,
        username = details.username,
        externalId = details.externalId,
        firstName = details.firstName,
        gender = fromLibGender(details.gender),
        groups = details.groups?.map {
            fromLibGroupInfo(it)
        } ?: listOf(),
        id = details.id,
        lastLoginTime = details.lastLoginTime ?: "",
        lastName = details.lastName,
        name = details.name,
        nickname = details.nickname,
        phone = details.phone,
        picture = details.picture,
        registrationTime = details.registrationTime ?: "",
        isAnonymous = details.isAnonymous,
        tag = details.tag
    )

data class BanInfo(
    val dateFrom: String,
    val dateTo: String?,
    val reason: String?
)

internal fun fromLibBanInfo(banInfo: com.xsolla.lib_login.entity.response.BanInfo?): BanInfo? {
    if (banInfo == null) return null
    return BanInfo(
        dateFrom = banInfo.dateFrom,
        dateTo = banInfo.dateTo,
        reason = banInfo.reason
    )
}

data class GroupInfo(
    val id: Int,
    val is_default: Boolean,
    val is_deletable: Boolean,
    val name: String
)

internal fun fromLibGroupInfo(groupInfo: com.xsolla.lib_login.entity.response.GroupInfo): GroupInfo =
    GroupInfo(
        id = groupInfo.id,
        is_default = groupInfo.is_default,
        is_deletable = groupInfo.is_deletable,
        name = groupInfo.name
    )

enum class GenderResponse {
    F,
    M,
    OTHER,
    PREFER_NOT_TO_ANSWER
}

internal fun fromLibGender(gender: com.xsolla.lib_login.entity.response.GenderResponse?): GenderResponse? {
    if (gender == null) {
        return null
    }
    return when (gender) {
        com.xsolla.lib_login.entity.response.GenderResponse.F -> GenderResponse.F
        com.xsolla.lib_login.entity.response.GenderResponse.M -> GenderResponse.M
        com.xsolla.lib_login.entity.response.GenderResponse.OTHER -> GenderResponse.OTHER
        com.xsolla.lib_login.entity.response.GenderResponse.PREFER_NOT_TO_ANSWER -> GenderResponse.PREFER_NOT_TO_ANSWER
    }
}

data class PhoneResponse(val phone: String?)

data class PictureResponse(val picture: String)
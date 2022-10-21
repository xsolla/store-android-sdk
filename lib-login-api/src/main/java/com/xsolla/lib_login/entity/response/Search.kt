package com.xsolla.lib_login.entity.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class SearchUsersByNicknameResponse(
    @SerialName("offset")
    val offset: Int,
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("users")
    val users: List<UserFromSearch>
)

@Serializable
internal data class UserFromSearch(
    @SerialName("avatar")
    val avatar: String?,
    @SerialName("is_me")
    val isCurrentUser: Boolean,
    @SerialName("last_login")
    val lastLoginTime: String,
    @SerialName("nickname")
    val nickname: String,
    @SerialName("registered")
    val registeredTime: String,
    @SerialName("user_id")
    val xsollaUserId: String,
    @SerialName("tag")
    val tag: String?
)

@Serializable
internal data class UserPublicInfoResponse(
    @SerialName("avatar")
    val avatar: String?,
    @SerialName("last_login")
    val lastLoginTime: String,
    @SerialName("nickname")
    val nickname: String?,
    @SerialName("registered")
    val registeredTime: String,
    @SerialName("user_id")
    val xsollaUserId: String,
    @SerialName("tag")
    val tag:String?
)
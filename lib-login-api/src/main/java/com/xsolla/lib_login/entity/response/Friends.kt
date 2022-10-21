package com.xsolla.lib_login.entity.response

import com.xsolla.lib_login.entity.common.SocialNetwork
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class UserFriendsResponse(
    @SerialName("next_after")
    val nextAfter: String?,
    @SerialName("next_url")
    val nextUrl: String?,
    @SerialName("relationships")
    val relationships: List<UserFriendsResponseRelationship>
)

@Serializable
internal data class UserFriendsResponseRelationship(
    @SerialName("presence")
    val presence: Presence?,
    @SerialName("status_incoming")
    val statusIncoming: FriendStatusResponse,
    @SerialName("status_outgoing")
    val statusOutgoing: FriendStatusResponse,
    @SerialName("updated")
    val updated: Double?,
    @SerialName("user")
    val user: UserDetailsResponse
)

@Serializable
internal enum class Presence {
    @SerialName("online")
    ONLINE,
    @SerialName("offline")
    OFFLINE
}

@Serializable
internal enum class FriendStatusResponse {
    @SerialName("none")
    NONE,
    @SerialName("friend")
    FRIEND,
    @SerialName("friend_requested")
    FRIEND_REQUESTED,
    @SerialName("blocked")
    BLOCKED
}

@Serializable
internal data class SocialFriendsResponse(
    @SerialName("data")
    val friendsList: List<SocialFriend>,
    @SerialName("limit")
    val limit: Int,
    @SerialName("offset")
    val offset: Int,
    @SerialName("platform")
    val platform: SocialNetwork?,
    @SerialName("total_count")
    val totalCount: Int,
    @SerialName("with_xl_uid")
    val withXlUid: Boolean?
)

@Serializable
internal data class SocialFriend(
    @SerialName("avatar")
    val avatar: String?,
    @SerialName("name")
    val name: String,
    @SerialName("platform")
    val platform: SocialNetwork?,
    @SerialName("user_id")
    val socialNetworkUserId: String,
    @SerialName("xl_uid")
    val xsollaUserId: String?,
    @SerialName("tag")
    val tag: String?
)
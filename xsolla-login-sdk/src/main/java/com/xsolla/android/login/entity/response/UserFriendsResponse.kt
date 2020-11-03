package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class UserFriendsResponse(
    @SerializedName("next_after")
    val nextAfter: String? = null,
    @SerializedName("next_url")
    val nextUrl: String? = null,
    val relationships: List<UserFriendsResponseRelationship> = emptyList()
)

data class UserFriendsResponseRelationship(
    val presence: Presence?,
    @SerializedName("status_incoming")
    val statusIncoming: FriendStatusResponse,
    @SerializedName("status_outgoing")
    val statusOutgoing: FriendStatusResponse,
    val updated: Double?,
    val user: UserDetailsResponse
)

enum class Presence {
    @SerializedName("online")
    ONLINE,
    @SerializedName("offline")
    OFFLINE
}

enum class FriendStatusResponse {
    @SerializedName("none")
    NONE,
    @SerializedName("friend")
    FRIEND,
    @SerializedName("friend_requested")
    FRIEND_REQUESTED,
    @SerializedName("blocked")
    BLOCKED
}
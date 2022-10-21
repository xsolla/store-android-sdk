package com.xsolla.android.login.entity.response

data class UserFriendsResponse(
    val nextAfter: String? = null,
    val nextUrl: String? = null,
    val relationships: List<UserFriendsResponseRelationship> = emptyList()
)

internal fun fromLibUserFriendsResponse(
    response: com.xsolla.lib_login.entity.response.UserFriendsResponse
) = UserFriendsResponse(
    nextAfter = response.nextAfter,
    nextUrl = response.nextUrl,
    relationships = response.relationships.map {
        fromLibUserFriendsResponseRelationship(it)
    }
)


data class UserFriendsResponseRelationship(
    val presence: Presence?,
    val statusIncoming: FriendStatusResponse,
    val statusOutgoing: FriendStatusResponse,
    val updated: Double?,
    val user: UserDetailsResponse
)

internal fun fromLibUserFriendsResponseRelationship(
    response: com.xsolla.lib_login.entity.response.UserFriendsResponseRelationship
): UserFriendsResponseRelationship {
    return UserFriendsResponseRelationship(
        presence = when (response.presence) {
            com.xsolla.lib_login.entity.response.Presence.ONLINE -> Presence.ONLINE
            com.xsolla.lib_login.entity.response.Presence.OFFLINE -> Presence.OFFLINE
            null -> null
        },
        statusIncoming = fromLibFriendStatusResponse(response.statusIncoming),
        statusOutgoing = fromLibFriendStatusResponse(response.statusOutgoing),
        updated = response.updated,
        user = fromLibUserDetails(response.user)
    )
}

enum class Presence {
    ONLINE,
    OFFLINE
}

enum class FriendStatusResponse {
    NONE,
    FRIEND,
    FRIEND_REQUESTED,
    BLOCKED
}

internal fun fromLibFriendStatusResponse(
    response: com.xsolla.lib_login.entity.response.FriendStatusResponse
) = when (response) {
    com.xsolla.lib_login.entity.response.FriendStatusResponse.NONE -> FriendStatusResponse.NONE
    com.xsolla.lib_login.entity.response.FriendStatusResponse.FRIEND -> FriendStatusResponse.FRIEND
    com.xsolla.lib_login.entity.response.FriendStatusResponse.FRIEND_REQUESTED -> FriendStatusResponse.FRIEND_REQUESTED
    com.xsolla.lib_login.entity.response.FriendStatusResponse.BLOCKED -> FriendStatusResponse.BLOCKED
}
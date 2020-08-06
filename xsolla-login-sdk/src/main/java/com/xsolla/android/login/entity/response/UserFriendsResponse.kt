package com.xsolla.android.login.entity.response

data class UserFriendsResponse(
    val relationships: List<UserFriendsResponseRelationship>
)

data class UserFriendsResponseRelationship(
    val presence: String,
    val status_incoming: String,
    val status_outgoing: String,
    val updated: String,
    val user: UserDetailsResponse
)
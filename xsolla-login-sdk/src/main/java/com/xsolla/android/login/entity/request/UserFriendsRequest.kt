package com.xsolla.android.login.entity.request

enum class UserFriendsRequestType {
    FRIENDS,
    FRIEND_REQUESTED,
    FRIEND_REQUESTED_BY,
    BLOCKED,
    BLOCKED_BY
}

enum class UserFriendsRequestSortBy {
    BY_NAME,
    BY_UPDATED
}

enum class UserFriendsRequestSortOrder {
    ASC,
    DESC
}
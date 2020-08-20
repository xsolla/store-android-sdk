package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

data class UserFriendsRequest(
        val type: String,
        @SerializedName("sort_by")
        val sortBy: String,
        @SerializedName("sort_order")
        val sortOrder: String
)

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
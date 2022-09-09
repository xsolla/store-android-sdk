package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName

internal data class UpdateUserFriendsRequest(
    val action: String,
    @SerializedName("user")
    val userId: String
)

enum class UpdateUserFriendsRequestAction {
    FRIEND_REQUEST_ADD,
    FRIEND_REQUEST_CANCEL,
    FRIEND_REQUEST_APPROVE,
    FRIEND_REQUEST_DENY,
    FRIEND_REMOVE,
    BLOCK,
    UNBLOCK
}
package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class SearchUsersByNicknameResponse(
        val offset: Int,
        @SerializedName("total_count")
        val totalCount: Int,
        val users: List<UserFromSearch>
)

data class UserFromSearch(
        val avatar: String?,
        @SerializedName("is_me")
        val isCurrentUser: Boolean,
        @SerializedName("last_login")
        val lastLoginTime: String,
        val nickname: String,
        @SerializedName("registered")
        val registeredTime: String,
        @SerializedName("user_id")
        val xsollaUserId: String,
        @SerializedName("tag")
        val tag: String?
)
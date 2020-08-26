package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName

data class SocialFriendsResponse(
        @SerializedName("data")
        val friendsList: List<SocialFriend>,
        val limit: Int,
        val offset: Int,
        val platform: String?,
        @SerializedName("total_count")
        val totalCount: Int
);

data class SocialFriend(
    val avatar: String?,
    val name: String,
    val platform: String,
    @SerializedName("user_id")
    val socialNetworkUserId: String,
    @SerializedName("xl_uid")
    val xsollaUserId: String?
)
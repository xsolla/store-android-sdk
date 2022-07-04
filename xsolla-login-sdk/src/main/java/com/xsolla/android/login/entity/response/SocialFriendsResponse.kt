package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName
import com.xsolla.android.login.social.SocialNetwork

data class SocialFriendsResponse(
    @SerializedName("data")
    val friendsList: List<SocialFriend>,
    val limit: Int,
    val offset: Int,
    val platform: SocialNetwork?,
    @SerializedName("total_count")
    val totalCount: Int,
    @SerializedName("with_xl_uid")
    val withXlUid: Boolean
)

data class SocialFriend(
    val avatar: String?,
    val name: String,
    val platform: SocialNetwork?,
    @SerializedName("user_id")
    val socialNetworkUserId: String,
    @SerializedName("xl_uid")
    val xsollaUserId: String?,
    @SerializedName("tag")
    val tag: String?
)
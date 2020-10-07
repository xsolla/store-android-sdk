package com.xsolla.android.login.entity.response

import com.google.gson.annotations.SerializedName
import com.xsolla.android.login.social.SocialNetworkForLinking

data class SocialFriendsResponse(
    @SerializedName("data")
    val friendsList: List<SocialFriend>,
    val limit: Int,
    val offset: Int,
    val platform: SocialNetworkForLinking?,
    @SerializedName("total_count")
    val totalCount: Int
)

data class SocialFriend(
    val avatar: String?,
    val name: String,
    val platform: SocialNetworkForLinking,
    @SerializedName("user_id")
    val socialNetworkUserId: String,
    @SerializedName("xl_uid")
    val xsollaUserId: String?
)
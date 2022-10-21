package com.xsolla.android.login.entity.response

import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.login.social.fromLibSocialNetwork

data class SocialFriendsResponse(
    val friendsList: List<SocialFriend>,
    val limit: Int,
    val offset: Int,
    val platform: SocialNetwork?,
    val totalCount: Int,
    val withXlUid: Boolean
)

internal fun fromLibSocialFriendsResponse(libResponse: com.xsolla.lib_login.entity.response.SocialFriendsResponse) =
    SocialFriendsResponse(
        friendsList = libResponse.friendsList.map {
            fromLibSocialFriend(it)
        },
        limit = libResponse.limit,
        offset = libResponse.offset,
        platform = fromLibSocialNetwork(libResponse.platform),
        totalCount = libResponse.totalCount,
        withXlUid = libResponse.withXlUid ?: false
    )

data class SocialFriend(
    val avatar: String?,
    val name: String,
    val platform: SocialNetwork?,
    val socialNetworkUserId: String,
    val xsollaUserId: String?,
    val tag: String?
)

internal fun fromLibSocialFriend(libSocialFriend: com.xsolla.lib_login.entity.response.SocialFriend) =
    SocialFriend(
        avatar = libSocialFriend.avatar,
        name = libSocialFriend.name,
        platform = fromLibSocialNetwork(libSocialFriend.platform),
        socialNetworkUserId = libSocialFriend.socialNetworkUserId,
        xsollaUserId = libSocialFriend.xsollaUserId,
        tag = libSocialFriend.tag
    )
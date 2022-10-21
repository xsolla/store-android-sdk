package com.xsolla.android.login.entity.response

import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.login.social.fromLibSocialNetwork

data class LinkedSocialNetworkResponse(
    val fullName: String?,
    val nickname: String?,
    val picture: String?,
    val socialNetwork: SocialNetwork?,
    val socialId: String
)

internal fun fromLibLinkedSocialNetworkResponse(response: com.xsolla.lib_login.entity.response.LinkedSocialNetworkResponse) =
    LinkedSocialNetworkResponse(
        fullName = response.fullName,
        nickname = response.nickname,
        picture = response.picture,
        socialNetwork = fromLibSocialNetwork(response.socialNetwork),
        socialId = response.socialId
    )
package com.xsolla.android.login.entity.response

import com.xsolla.android.login.social.SocialNetwork
import com.xsolla.android.login.social.fromLibSocialNetwork

data class LinksForSocialAuthResponse(
    val links : List<LinksItem> = emptyList()
)

internal fun fromLibLinksForSocialAuthResponse(response: com.xsolla.lib_login.entity.response.LinksForSocialAuthResponse) =
    LinksForSocialAuthResponse(
        response.links.map {
            LinksItem(
                authUrl = it.authUrl,
                provider = fromLibSocialNetwork(it.provider)
            )
        }
    )

data class LinksItem(
    val authUrl:String,
    val provider: SocialNetwork?
)

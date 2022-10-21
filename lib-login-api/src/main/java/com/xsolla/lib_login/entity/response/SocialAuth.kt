package com.xsolla.lib_login.entity.response

import com.xsolla.lib_login.entity.common.SocialNetwork
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class LinkForSocialAuthResponse(
    @SerialName("url")
    val url: String
)

@Serializable
internal data class LinksForSocialAuthResponse(
    @SerialName("links")
    val links: List<LinksItem> = emptyList()
)

@Serializable
internal data class LinksItem(
    @SerialName("auth_url")
    val authUrl: String,
    @SerialName("provider")
    val provider: SocialNetwork?
)

@Serializable
internal data class LinkedSocialNetworkResponse(
    @SerialName("full_name")
    val fullName: String?,

    @SerialName("nickname")
    val nickname: String?,

    @SerialName("picture")
    val picture: String?,

    @SerialName("provider")
    val socialNetwork: SocialNetwork?,

    @SerialName("social_id")
    val socialId: String
)

@Serializable
internal data class UrlToLinkSocialNetworkResponse(
    @SerialName("url")
    val url: String
)
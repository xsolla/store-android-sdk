package com.xsolla.android.login.entity.response

data class SearchUsersByNicknameResponse(
        val offset: Int,
        val totalCount: Int,
        val users: List<UserFromSearch>
)

internal fun fromLibSearch(libResponse: com.xsolla.lib_login.entity.response.SearchUsersByNicknameResponse) =
        SearchUsersByNicknameResponse(
                offset = libResponse.offset,
                totalCount = libResponse.totalCount,
                users = libResponse.users.map {
                        UserFromSearch(
                                avatar = it.avatar,
                                isCurrentUser = it.isCurrentUser,
                                lastLoginTime = it.lastLoginTime,
                                nickname = it.nickname,
                                registeredTime = it.registeredTime,
                                xsollaUserId = it.xsollaUserId,
                                tag = it.tag
                        )
                }
        )

data class UserFromSearch(
        val avatar: String?,
        val isCurrentUser: Boolean,
        val lastLoginTime: String,
        val nickname: String,
        val registeredTime: String,
        val xsollaUserId: String,
        val tag: String?
)
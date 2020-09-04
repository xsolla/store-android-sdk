package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName
import com.xsolla.android.login.entity.common.UserAttribute
import com.xsolla.android.login.entity.common.UserAttributePermission

data class GetUsersAttributesFromClientRequest(
    val keys: List<String> = emptyList(),
    @SerializedName("publisher_project_id")
    val publisherProjectId: Int? = null,
    @SerializedName("user_id")
    val userId: String? = null
)

data class UpdateUsersAttributesFromClientRequest(
    val attributes: List<UserAttribute> = emptyList(),
    @SerializedName("publisher_project_id")
    val publisherProjectId: Int? = null,
    @SerializedName("removing_keys")
    val removingKeys: List<String> = emptyList()
)

data class UpdateUsersAttributesFromServerRequest(
    val attributes: List<UserAttributeServer> = emptyList(),
    @SerializedName("publisher_id")
    val publisherId: Int,
    @SerializedName("publisher_project_id")
    val publisherProjectId: Int? = null,
    @SerializedName("removing_keys")
    val removingKeys: List<String> = emptyList()
)

data class UserAttributeServer(
    @SerializedName("attr_type")
    val attributeType: UserAttributeType,
    val key: String,
    val permission: UserAttributePermission,
    val value: String
)

enum class UserAttributeType {
    @SerializedName("client")
    CLIENT,
    @SerializedName("server")
    SERVER
}
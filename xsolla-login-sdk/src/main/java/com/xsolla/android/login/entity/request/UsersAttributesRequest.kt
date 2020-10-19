package com.xsolla.android.login.entity.request

import com.google.gson.annotations.SerializedName
import com.xsolla.android.login.entity.common.UserAttribute

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
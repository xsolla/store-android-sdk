package com.xsolla.lib_login.entity.request

import com.xsolla.lib_login.entity.common.UserAttribute
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class GetAttributesBody(
    @SerialName("keys")
    val keys: List<String>,
    @SerialName("publisher_project_id")
    val publisherProjectId: Int?,
    @SerialName("user_id")
    val userId: String?
)

@Serializable
internal data class UpdateAttributesBody(
    @SerialName("attributes")
    val attributes: List<UserAttribute>,
    @SerialName("publisher_project_id")
    val publisherProjectId: Int?,
    @SerialName("removing_keys")
    val removingKeys: List<String>
)
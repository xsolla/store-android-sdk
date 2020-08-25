package com.xsolla.android.store.entity.response.gropus

import com.google.gson.annotations.SerializedName

data class ItemsGroupsResponse(val groups: List<Group> = emptyList()) {
    data class Group(
        @SerializedName("external_id")
        val externalId: String? = null,
        val name: String? = null,
        val description: String? = null,
        @SerializedName("image_url")
        val imageUrl: String? = null,
        val order: Int,
        val level: Int,
        val children: List<Group> = emptyList(),
        @SerializedName("parent_external_id")
        val parentExternalId: String? = null
    )
}
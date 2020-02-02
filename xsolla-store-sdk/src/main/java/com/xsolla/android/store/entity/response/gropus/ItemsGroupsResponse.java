package com.xsolla.android.store.entity.response.gropus;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ItemsGroupsResponse {

    private List<Group> groups;

    private class Group {
        @SerializedName("external_id")
        private String externalId;
        private String name;
        private String description;
        @SerializedName("image_url")
        private String imageUrl;
        private int order;
        private int level;
        private List<Group> children;
        @SerializedName("parent_external_id")
        private String parentExternalId;
    }

}

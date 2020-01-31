package com.xsolla.android.store.entity.response.items.fields;

import com.google.gson.annotations.SerializedName;

public class Group {

    @SerializedName("external_id")
    private String externalId;
    private String name;

    public String getExternalId() {
        return externalId;
    }

    public String getName() {
        return name;
    }
}

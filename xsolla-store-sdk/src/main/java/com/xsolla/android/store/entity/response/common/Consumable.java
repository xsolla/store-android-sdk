package com.xsolla.android.store.entity.response.common;

import com.google.gson.annotations.SerializedName;

class Consumable {

    @SerializedName("usages_count")
    private int usagesCount;

    public int getUsagesCount() {
        return usagesCount;
    }
}

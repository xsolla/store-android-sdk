package com.xsolla.android.store.entity.request.inventory;

import com.google.gson.annotations.SerializedName;

public class ConsumeItemBody {

    private String sku;
    private long quantity;
    @SerializedName("instance_id")
    private String instanceId;

    public ConsumeItemBody(String sku, long quantity, String instanceId) {
        this.sku = sku;
        this.quantity = quantity;
        this.instanceId = instanceId;
    }
}

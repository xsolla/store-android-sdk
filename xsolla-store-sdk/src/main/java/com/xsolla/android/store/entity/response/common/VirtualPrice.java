package com.xsolla.android.store.entity.response.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class VirtualPrice implements Parcelable {

    private int amount;

    @SerializedName("amount_without_discount")
    private int amountWithoutDiscount;
    private String sku;

    @SerializedName("is_default")
    private boolean isDefault;

    @SerializedName("image_url")
    private String imageUrl;
    private String name;
    private String type;
    private String description;

    protected VirtualPrice(Parcel in) {
        amount = in.readInt();
        amountWithoutDiscount = in.readInt();
        sku = in.readString();
        isDefault = in.readByte() != 0;
        imageUrl = in.readString();
        name = in.readString();
        type = in.readString();
        description = in.readString();
    }

    public static final Creator<VirtualPrice> CREATOR = new Creator<VirtualPrice>() {
        @Override
        public VirtualPrice createFromParcel(Parcel in) {
            return new VirtualPrice(in);
        }

        @Override
        public VirtualPrice[] newArray(int size) {
            return new VirtualPrice[size];
        }
    };

    public int getAmount() {
        return amount;
    }

    public int getAmountWithoutDiscount() {
        return amountWithoutDiscount;
    }

    public String getSku() {
        return sku;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getPrettyPrintAmount() {
        return getAmount() + " " + getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(amount);
        dest.writeInt(amountWithoutDiscount);
        dest.writeString(sku);
        dest.writeByte((byte) (isDefault ? 1 : 0));
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(description);
    }
}

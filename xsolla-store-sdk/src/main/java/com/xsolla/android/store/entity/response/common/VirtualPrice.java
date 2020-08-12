package com.xsolla.android.store.entity.response.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

public class VirtualPrice implements IPrice, Parcelable {

    private long amount;
    @SerializedName("amount_without_discount")
    private int amountWithoutDiscount;

    @SerializedName("calculated_price")
    private CalculatedPrice calculatedPrice;

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
        calculatedPrice = (CalculatedPrice) in.readSerializable();
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

    @Deprecated
    public long getAmount() {
        return amount;
    }

    @Deprecated
    public int getAmountWithoutDiscount() {
        return amountWithoutDiscount;
    }

    @Override
    public String getAmountRaw() {
        return calculatedPrice.amount.toPlainString();
    }

    @Override
    public String getAmountWithoutDiscountRaw() {
        return calculatedPrice.amountWithoutDiscount.toPlainString();
    }

    @Override
    public BigDecimal getAmountDecimal() {
        return calculatedPrice.amount;
    }

    @Override
    public BigDecimal getAmountWithoutDiscountDecimal() {
        return calculatedPrice.amountWithoutDiscount;
    }

    @Override
    public String getCurrencyId() {
        return getSku();
    }

    @Override
    public String getCurrencyName() {
        return getName();
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

    @Deprecated
    public String getPrettyPrintAmount() {
        return getAmountDecimal().stripTrailingZeros().toPlainString() + " " + getName();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(amount);
        dest.writeInt(amountWithoutDiscount);
        dest.writeString(sku);
        dest.writeByte((byte) (isDefault ? 1 : 0));
        dest.writeString(imageUrl);
        dest.writeString(name);
        dest.writeString(type);
        dest.writeString(description);
        dest.writeSerializable(calculatedPrice);
    }

    public static class CalculatedPrice implements Serializable {
        public BigDecimal amount;
        @SerializedName("amount_without_discount")
        public BigDecimal amountWithoutDiscount;
    }
}

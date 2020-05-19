package com.xsolla.android.store.entity.response.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class InventoryOption implements Parcelable {

    private Consumable consumable;

    @SerializedName("expiration_period")
    private ExpirationPeriod expirationPeriod;

    protected InventoryOption(Parcel in) {
        consumable = in.readParcelable(Consumable.class.getClassLoader());
        expirationPeriod = in.readParcelable(ExpirationPeriod.class.getClassLoader());
    }

    public static final Creator<InventoryOption> CREATOR = new Creator<InventoryOption>() {
        @Override
        public InventoryOption createFromParcel(Parcel in) {
            return new InventoryOption(in);
        }

        @Override
        public InventoryOption[] newArray(int size) {
            return new InventoryOption[size];
        }
    };

    public Consumable getConsumable() {
        return consumable;
    }

    public ExpirationPeriod getExpirationPeriod() {
        return expirationPeriod;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(consumable, flags);
        dest.writeParcelable(expirationPeriod, flags);
    }
}

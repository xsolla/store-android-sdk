package com.xsolla.android.store.entity.response.common;

import android.os.Parcel;
import android.os.Parcelable;

public class InventoryOption implements Parcelable {

    private Consumable consumable;

    protected InventoryOption(Parcel in) {
        consumable = in.readParcelable(Consumable.class.getClassLoader());
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(consumable, flags);
    }
}

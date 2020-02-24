package com.xsolla.android.store.entity.response.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

class Consumable implements Parcelable {

    @SerializedName("usages_count")
    private int usagesCount;

    protected Consumable(Parcel in) {
        usagesCount = in.readInt();
    }

    public static final Creator<Consumable> CREATOR = new Creator<Consumable>() {
        @Override
        public Consumable createFromParcel(Parcel in) {
            return new Consumable(in);
        }

        @Override
        public Consumable[] newArray(int size) {
            return new Consumable[size];
        }
    };

    public int getUsagesCount() {
        return usagesCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(usagesCount);
    }
}

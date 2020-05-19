package com.xsolla.android.store.entity.response.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class ExpirationPeriod implements Parcelable {

    private Type type;
    private int value;

    protected ExpirationPeriod(Parcel in) {
        type = Type.valueOf(in.readString());
        value = in.readInt();
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

    public Type getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type.name());
        dest.writeInt(value);
    }

    public enum Type {
        @SerializedName("minute")
        MINUTE,
        @SerializedName("hour")
        HOUR,
        @SerializedName("day")
        DAY,
        @SerializedName("week")
        WEEK,
        @SerializedName("month")
        MONTH,
        @SerializedName("year")
        YEAR
    }
}

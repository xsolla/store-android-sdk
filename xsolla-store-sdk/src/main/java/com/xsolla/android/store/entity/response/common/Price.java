package com.xsolla.android.store.entity.response.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Price implements Parcelable {

    private String amount;

    @SerializedName("amount_without_discount")
    private String amountWithoutDiscount;
    private String currency;

    protected Price(Parcel in) {
        amount = in.readString();
        amountWithoutDiscount = in.readString();
        currency = in.readString();
    }

    public static final Creator<Price> CREATOR = new Creator<Price>() {
        @Override
        public Price createFromParcel(Parcel in) {
            return new Price(in);
        }

        @Override
        public Price[] newArray(int size) {
            return new Price[size];
        }
    };

    public String getAmount() {
        return amount;
    }

    public String getAmountWithoutDiscount() {
        return amountWithoutDiscount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPrettyPrintAmount() {
        return getAmount().substring(0, getAmount().indexOf(".") + 3) + " " + getCurrency();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(amount);
        dest.writeString(amountWithoutDiscount);
        dest.writeString(currency);
    }
}

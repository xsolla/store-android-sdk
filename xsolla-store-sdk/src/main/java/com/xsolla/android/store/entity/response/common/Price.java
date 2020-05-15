package com.xsolla.android.store.entity.response.common;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Price implements IPrice, Parcelable {

    private BigDecimal amount;
    @SerializedName("amount_without_discount")
    private BigDecimal amountWithoutDiscount;

    private String currency;

    protected Price(Parcel in) {
        amount = (BigDecimal) in.readSerializable();
        amountWithoutDiscount = (BigDecimal) in.readSerializable();
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

    @Deprecated
    public String getAmount() {
        return amount.toPlainString();
    }

    @Deprecated
    public String getAmountWithoutDiscount() {
        return amountWithoutDiscount.toPlainString();
    }

    @Override
    public String getAmountRaw() {
        return amount.toPlainString();
    }

    @Override
    public String getAmountWithoutDiscountRaw() {
        return amountWithoutDiscount.toPlainString();
    }

    @Override
    public BigDecimal getAmountDecimal() {
        return amount;
    }

    @Override
    public BigDecimal getAmountWithoutDiscountDecimal() {
        return amountWithoutDiscount;
    }

    @Override
    public String getCurrencyId() {
        return getCurrency();
    }

    @Override
    public String getCurrencyName() {
        return getCurrency();
    }

    public String getCurrency() {
        return currency;
    }

    @Deprecated
    public String getPrettyPrintAmount() {
        return getAmountDecimal().setScale(2, RoundingMode.HALF_UP).toPlainString() + " " + getCurrency();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(amount);
        dest.writeSerializable(amountWithoutDiscount);
        dest.writeString(currency);
    }
}

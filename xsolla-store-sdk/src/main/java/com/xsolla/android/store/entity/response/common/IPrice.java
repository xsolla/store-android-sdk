package com.xsolla.android.store.entity.response.common;

import java.math.BigDecimal;

public interface IPrice {

    String getCurrencyId();

    String getCurrencyName();

    String getAmountRaw();

    String getAmountWithoutDiscountRaw();

    BigDecimal getAmountDecimal();

    BigDecimal getAmountWithoutDiscountDecimal();

}

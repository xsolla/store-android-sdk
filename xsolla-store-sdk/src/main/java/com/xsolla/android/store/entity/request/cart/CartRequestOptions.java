package com.xsolla.android.store.entity.request.cart;

public class CartRequestOptions {

    private String currency = null;
    private String locale = null;

    public String getCurrency() {
        return currency;
    }

    public String getLocale() {
        return locale;
    }

    public Builder create() {
        return new CartRequestOptions().new Builder();
    }

    public class Builder {

        private Builder() {

        }

        public Builder setCurrency(String currency) {
            CartRequestOptions.this.currency = currency;
            return this;
        }

        public Builder setLocale(String locale) {
            CartRequestOptions.this.locale = locale;
            return this;
        }

        public CartRequestOptions build() {
            return CartRequestOptions.this;
        }

    }
}


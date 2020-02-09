package com.xsolla.android.store.entity.request.payment;

public class PaymentOptions {

    private String currency;
    private String locale;
    private boolean sandbox;

    public String getCurrency() {
        return currency;
    }

    public String getLocale() {
        return locale;
    }

    public boolean isSandbox() {
        return sandbox;
    }

    public Builder create() {
        return new PaymentOptions().new Builder();
    }

    public class Builder {

        private Builder() {

        }

        public Builder setCurrency(String currency) {
            PaymentOptions.this.currency = currency;
            return this;
        }

        public Builder setLocale(String locale) {
            PaymentOptions.this.locale = locale;
            return this;
        }

        public Builder setSandbox(boolean sandbox) {
            PaymentOptions.this.sandbox = sandbox;
            return this;
        }

        public PaymentOptions build() {
            return PaymentOptions.this;
        }

    }
}

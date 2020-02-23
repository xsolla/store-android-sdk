package com.xsolla.android.store.entity.request.payment;

public class CreateOrderRequestBody {

    private String currency;
    private String locale;
    private boolean sandbox;

    private CreateOrderRequestBody(String currency, String locale, boolean sandbox) {
        this.currency = currency;
        this.locale = locale;
        this.sandbox = sandbox;
    }

    public static CreateOrderRequestBody create(PaymentOptions options) {
        PaymentOptions requestOptions;
        if (options != null) {
            requestOptions = options;
        } else {
            requestOptions = new PaymentOptions().create().build();
        }

        return new CreateOrderRequestBody(
                requestOptions.getCurrency(),
                requestOptions.getLocale(),
                requestOptions.isSandbox()
        );
    }
}

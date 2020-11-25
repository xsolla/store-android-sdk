package com.xsolla.android.storesdkexample.listener;

import android.content.Intent;

public interface CreatePaystationIntentListener {

    void onIntentCreated(Intent intent, String externalId);

    void onFailure(String message);

}

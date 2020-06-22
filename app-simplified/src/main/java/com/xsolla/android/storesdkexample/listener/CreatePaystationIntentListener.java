package com.xsolla.android.storesdkexample.listener;

import android.content.Intent;

public interface CreatePaystationIntentListener {

    void onIntentCreated(Intent intent);

    void onFailure(String message);

}

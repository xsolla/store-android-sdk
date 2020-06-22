package com.xsolla.android.storesdkexample.util;

import android.view.View;

public class ViewUtils {

    private static final long DELAY_TIME = 1000L;

    public static void disable(View view) {
        view.setEnabled(false);
    }

    public static void enable(View view) {
        view.setEnabled(true);
    }
}

package com.xsolla.android.xsolla_login_sdk.token;

import android.net.UrlQuerySanitizer;

public class TokenUtils {

    public static String getTokenFromUrl(String url) {
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
        sanitizer.setAllowUnregisteredParamaters(true);
        sanitizer.parseUrl(url);
        return sanitizer.getValue("token");
    }

}

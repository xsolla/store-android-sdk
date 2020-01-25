package com.xsolla.android.xsolla_login_sdk.token;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.UrlQuerySanitizer;

import com.xsolla.android.xsolla_login_sdk.jwt.JWT;


public class TokenUtils {

    private SharedPreferences preferences;
    private JWT jwt;

    public TokenUtils(Activity activity) {
        this.preferences = activity.getPreferences(Context.MODE_PRIVATE);
    }

    public JWT getJwt() {
        return jwt;
    }

    public void saveToken(String token) {
        jwt = new JWT(token);

        preferences
                .edit()
                .putString("jwtToken", token)
                .apply();
    }

    public String getToken() {
        return preferences.getString("jwtToken", null);
    }

    public void clearToken() {
        preferences
                .edit()
                .remove("jwtToken")
                .apply();
    }

    public static String getTokenFromUrl(String url) {
        UrlQuerySanitizer sanitizer = new UrlQuerySanitizer();
        sanitizer.setAllowUnregisteredParamaters(true);
        sanitizer.parseUrl(url);
        return sanitizer.getValue("token");
    }

}

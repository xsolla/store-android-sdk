package com.xsolla.android.login.token;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.xsolla.android.login.jwt.JWT;

public class TokenUtils {

    private static final String PREFS_FILE_NAME = "XSOLLA_LOGIN";

    private SharedPreferences preferences;

    public TokenUtils(Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences(PREFS_FILE_NAME, Context.MODE_PRIVATE);
    }

    public JWT getJwt() {
        String token = getToken();
        if (token == null) return null;
        return new JWT(token);
    }

    public void saveToken(String token) {
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
        Uri uri = Uri.parse(url);
        return uri.getQueryParameter("token");
    }

}

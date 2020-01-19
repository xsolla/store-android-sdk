package com.xsolla.android.xsolla_login_sdk.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.xsolla.android.xsolla_login_sdk.token.TokenUtils;

public class XWebView {

    private final static String XSOLLA_CALLBACK_URL = "https://login.xsolla.com/api/blank";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";

    public static void loadAuthPage(String loginUrl, Activity context) {
        WebView webView = createWebView(context);
        FrameLayout rootView = context.findViewById(android.R.id.content);

        rootView.addView(webView);
        webView.loadUrl(loginUrl);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private static WebView createWebView(Context context) {
        WebView webView = new WebView(context);
        webView.setWebViewClient(createWebViewClient());
        webView.getSettings().setUserAgentString(USER_AGENT);
        webView.getSettings().setJavaScriptEnabled(true);
        if (!webView.isInEditMode()) {
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
        }

        return webView;
    }

    private static WebViewClient createWebViewClient() {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.startsWith(XSOLLA_CALLBACK_URL)) {
                    String token = TokenUtils.getTokenFromUrl(url);
                    Log.d("TAG", token);
                }
            }
        };
    }

}

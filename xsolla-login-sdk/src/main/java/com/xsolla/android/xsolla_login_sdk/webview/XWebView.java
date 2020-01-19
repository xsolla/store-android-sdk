package com.xsolla.android.xsolla_login_sdk.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.listener.XSocialAuthListener;
import com.xsolla.android.xsolla_login_sdk.token.TokenUtils;

public class XWebView {

    private final static String XSOLLA_CALLBACK_URL = "https://login.xsolla.com/api/blank";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";

    public static void loadAuthPage(String loginUrl, XSocialAuthListener listener) {
        Activity context = null;

        if (listener instanceof Activity) {
            context = (Activity) listener;
        } else if (listener instanceof Fragment) {
            context = ((Fragment) listener).getActivity();
        }

        WebView webView = createWebView(context, listener);
        FrameLayout rootView = context.findViewById(android.R.id.content);

        rootView.addView(webView);
        webView.loadUrl(loginUrl);
    }


    @SuppressLint("SetJavaScriptEnabled")
    private static WebView createWebView(Context context, XSocialAuthListener listener) {
        WebView webView = new WebView(context);
        webView.setWebViewClient(createWebViewClient(listener));
        webView.getSettings().setUserAgentString(USER_AGENT);
        webView.getSettings().setJavaScriptEnabled(true);
        if (!webView.isInEditMode()) {
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
        }

        return webView;
    }

    private static WebViewClient createWebViewClient(final XSocialAuthListener listener) {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.startsWith(XSOLLA_CALLBACK_URL)) {
                    String token = TokenUtils.getTokenFromUrl(url);
                    XLogin.getInstance().setToken(token);
                    listener.onSocialLoginSuccess(token);
                }
            }
        };
    }

}

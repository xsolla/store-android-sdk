package com.xsolla.android.xsolla_login_sdk.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.xsolla.android.xsolla_login_sdk.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.listener.XSocialAuthListener;
import com.xsolla.android.xsolla_login_sdk.token.TokenUtils;

public class XWebView {

    private String callbackUrl = "https://login.xsolla.com/api/blank";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    private Activity context;
    private FrameLayout systemRootView;
    private View webViewLayout;

    private boolean isLoading = false;

    public XWebView(Activity context, String callbackUrl) {
        this.context = context;

        if (callbackUrl != null) {
            this.callbackUrl = callbackUrl;
        }
    }

    public void loadAuthPage(String loginUrl, XSocialAuthListener listener) {

        if (isLoading) return;
        isLoading = true;

        systemRootView = context.findViewById(android.R.id.content);
        webViewLayout = LayoutInflater.from(context).inflate(R.layout.x_web_view, null);
        WebView webView = webViewLayout.findViewById(R.id.web_view);
        initWebView(webView, listener);

        ImageView closeIcon = webViewLayout.findViewById(R.id.close_icon);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                systemRootView.removeView(webViewLayout);
            }
        });

        webView.loadUrl(loginUrl);
        systemRootView.addView(webViewLayout);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private WebView initWebView(WebView webView, XSocialAuthListener listener) {
        webView.setWebViewClient(createWebViewClient(listener));
        webView.getSettings().setUserAgentString(USER_AGENT);
        webView.getSettings().setJavaScriptEnabled(true);
        if (!webView.isInEditMode()) {
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setSupportZoom(true);
        }

        return webView;
    }

    private WebViewClient createWebViewClient(final XSocialAuthListener listener) {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                isLoading = false;
                if (url.startsWith(callbackUrl)) {
                    String token = TokenUtils.getTokenFromUrl(url);
                    XLogin.getInstance().saveToken(token);
                    systemRootView.removeView(webViewLayout);
                    listener.onSocialLoginSuccess(token);
                }
            }
        };
    }

}

package com.xsolla.android.xsolla_login_sdk.webview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.xsolla.android.xsolla_login_sdk.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.listener.XSocialAuthListener;
import com.xsolla.android.xsolla_login_sdk.token.TokenUtils;

public class XWebView {

    private final static String XSOLLA_CALLBACK_URL = "https://login.xsolla.com/api/blank";
    private static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
    private FrameLayout rootView;
    private RelativeLayout containerLayout;

    public void loadAuthPage(String loginUrl, XSocialAuthListener listener) {
        Activity context = null;

        if (listener instanceof Activity) {
            context = (Activity) listener;
        } else if (listener instanceof Fragment) {
            context = ((Fragment) listener).getActivity();
        }

        WebView webView = createWebView(context, listener);
        webView.loadUrl(loginUrl);

        rootView = context.findViewById(android.R.id.content);

        containerLayout = new RelativeLayout(context);
        ImageView closeIcon = new ImageView(context);
        closeIcon.setImageResource(R.drawable.close_webview_icon);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        closeIcon.setLayoutParams(params);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.removeView(containerLayout);
            }
        });

        containerLayout.addView(webView);
        containerLayout.addView(closeIcon);

        rootView.addView(containerLayout);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private WebView createWebView(Context context, XSocialAuthListener listener) {
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

    private WebViewClient createWebViewClient(final XSocialAuthListener listener) {
        return new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url.startsWith(XSOLLA_CALLBACK_URL)) {
                    String token = TokenUtils.getTokenFromUrl(url);
                    XLogin.getInstance().saveToken(token);
                    rootView.removeView(containerLayout);
                    listener.onSocialLoginSuccess(token);
                }
            }
        };
    }

}

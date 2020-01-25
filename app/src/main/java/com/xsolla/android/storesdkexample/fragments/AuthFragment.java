package com.xsolla.android.storesdkexample.fragments;

import android.view.View;
import android.widget.TextView;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.login.XLogin;
import com.xsolla.android.login.entity.request.Social;
import com.xsolla.android.login.listener.XAuthListener;
import com.xsolla.android.login.listener.XSocialAuthListener;

public class AuthFragment extends BaseFragment implements XAuthListener, XSocialAuthListener {


    @Override
    int getLayout() {
        return R.layout.fragment_auth;
    }

    @Override
    void initUI() {

        rootView.findViewById(R.id.google_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.getInstance().loginSocial(Social.GOOGLE, AuthFragment.this);
            }
        });

        rootView.findViewById(R.id.facebook_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.getInstance().loginSocial(Social.FACEBOOK, AuthFragment.this);
            }
        });

        rootView.findViewById(R.id.twitter_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.getInstance().loginSocial(Social.TWITTER, AuthFragment.this);
            }
        });

        rootView.findViewById(R.id.linkedin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.getInstance().loginSocial(Social.LINKEDIN, AuthFragment.this);
            }
        });

        rootView.findViewById(R.id.naver_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.getInstance().loginSocial(Social.NAVER, AuthFragment.this);
            }
        });

        final TextView usernameInput = rootView.findViewById(R.id.username_input);
        final TextView passwordInput = rootView.findViewById(R.id.password_input);

        rootView.findViewById(R.id.login_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();
                XLogin.getInstance().login(username, password, AuthFragment.this);
            }
        });

        rootView.findViewById(R.id.forgot_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new ResetPasswordFragment());
            }
        });

        rootView.findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new RegisterFragment());
            }
        });
    }

    @Override
    public void onLoginSuccess(String token) {
        openFragment(new ProfileFragment());
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        showSnack(errorMessage);
    }

    @Override
    public void onSocialLoginSuccess(String token) {
        openFragment(new ProfileFragment());
    }

    @Override
    public void onSocialLoginFailed(String errorMessage) {
        showSnack(errorMessage);
    }


}

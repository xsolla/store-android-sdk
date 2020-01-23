package com.xsolla.android.storesdkexample.fragments;

import android.view.View;
import android.widget.TextView;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.entity.request.LoginUser;
import com.xsolla.android.xsolla_login_sdk.entity.request.Social;
import com.xsolla.android.xsolla_login_sdk.listener.XAuthListener;
import com.xsolla.android.xsolla_login_sdk.listener.XSocialAuthListener;

public class AuthFragment extends BaseFragment implements XAuthListener, XSocialAuthListener {


    @Override
    int getLayout() {
        return R.layout.fragment_auth;
    }

    @Override
    void initUI() {
        rootView.findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new RegisterFragment());
            }
        });


        rootView.findViewById(R.id.forgot_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new ResetPasswordFragment());
            }
        });

        rootView.findViewById(R.id.auth_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((TextView) rootView.findViewById(R.id.username_input)).getText().toString();
                String password = ((TextView) rootView.findViewById(R.id.password_input)).getText().toString();
                LoginUser user = new LoginUser(username, password);
                XLogin.getInstance().login(user, AuthFragment.this);
            }
        });

        rootView.findViewById(R.id.facebook_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.getInstance().loginSocial(Social.FACEBOOK, AuthFragment.this);
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

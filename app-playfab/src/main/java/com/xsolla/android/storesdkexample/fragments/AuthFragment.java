package com.xsolla.android.storesdkexample.fragments;

import android.app.Activity;
import android.widget.TextView;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.login.api.XLoginCallback;
import com.xsolla.android.login.api.XLoginSocialCallback;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.SocialAuthResponse;
import com.xsolla.android.login.social.SocialNetwork;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;
import com.xsolla.android.storesdkexample.util.ViewUtils;

public class AuthFragment extends BaseFragment {


    @Override
    public int getLayout() {
        return R.layout.fragment_auth;
    }

    @Override
    public void initUI() {

        rootView.findViewById(R.id.google_button).setOnClickListener(v -> XLogin.loginSocial(SocialNetwork.GOOGLE, socialAuthCallback));

        rootView.findViewById(R.id.facebook_button).setOnClickListener(v -> XLogin.loginSocial(SocialNetwork.FACEBOOK, socialAuthCallback));

        rootView.findViewById(R.id.baidu_button).setOnClickListener(v -> XLogin.loginSocial(SocialNetwork.BAIDU, socialAuthCallback));

        rootView.findViewById(R.id.linkedin_button).setOnClickListener(v -> XLogin.loginSocial(SocialNetwork.LINKEDIN, socialAuthCallback));

        rootView.findViewById(R.id.naver_button).setOnClickListener(v -> XLogin.loginSocial(SocialNetwork.NAVER, socialAuthCallback));

        final TextView usernameInput = rootView.findViewById(R.id.username_input);
        final TextView passwordInput = rootView.findViewById(R.id.password_input);

        rootView.findViewById(R.id.login_button).setOnClickListener(v -> {
            ViewUtils.disable(v);

            hideKeyboard();
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            XLogin.login(username, password, new XLoginCallback<AuthResponse>() {
                @Override
                protected void onSuccess(AuthResponse response) {
                    openFragment(new MainFragment());
                    ViewUtils.enable(v);
                }

                @Override
                protected void onFailure(String errorMessage) {
                    showSnack(errorMessage);
                    ViewUtils.enable(v);
                }
            });
        });

        rootView.findViewById(R.id.forgot_password_button).setOnClickListener(v -> openFragment(new ResetPasswordFragment()));

        rootView.findViewById(R.id.register_button).setOnClickListener(v -> openFragment(new RegisterFragment()));
    }

    private XLoginSocialCallback<SocialAuthResponse> socialAuthCallback = new XLoginSocialCallback<SocialAuthResponse>() {
        @Override
        protected void onSuccess(SocialAuthResponse response) {
            openFragment(new MainFragment());
        }

        @Override
        protected void onFailure(String errorMessage) {
            showSnack(errorMessage);
        }

        @Override
        protected Activity getActivityForSocialAuth() {
            return getActivity();
        }
    };

}

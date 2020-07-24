package com.xsolla.android.storesdkexample.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.login.api.XLoginCallback;
import com.xsolla.android.login.callback.FinishSocialCallback;
import com.xsolla.android.login.callback.StartSocialCallback;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.social.SocialNetwork;
import com.xsolla.android.storesdkexample.BuildConfig;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;
import com.xsolla.android.storesdkexample.util.ViewUtils;

public class AuthFragment extends BaseFragment {

    private SocialNetwork selectedSocialNetwork;

    @Override
    public int getLayout() {
        return R.layout.fragment_auth;
    }

    @Override
    public void initUI() {

        rootView.findViewById(R.id.google_button).setOnClickListener(v -> {
            selectedSocialNetwork = SocialNetwork.GOOGLE;
            XLogin.startSocialAuth(this, SocialNetwork.GOOGLE, BuildConfig.WITH_LOGOUT, startSocialCallback);
        });

        rootView.findViewById(R.id.facebook_button).setOnClickListener(v -> {
            selectedSocialNetwork = SocialNetwork.FACEBOOK;
            XLogin.startSocialAuth(this, SocialNetwork.FACEBOOK, BuildConfig.WITH_LOGOUT, startSocialCallback);
        });

        rootView.findViewById(R.id.baidu_button).setOnClickListener(v -> {
            selectedSocialNetwork = SocialNetwork.BAIDU;
            XLogin.startSocialAuth(this, SocialNetwork.BAIDU, BuildConfig.WITH_LOGOUT, startSocialCallback);
        });

        rootView.findViewById(R.id.linkedin_button).setOnClickListener(v -> {
            selectedSocialNetwork = SocialNetwork.LINKEDIN;
            XLogin.startSocialAuth(this, SocialNetwork.LINKEDIN, BuildConfig.WITH_LOGOUT, startSocialCallback);
        });

        rootView.findViewById(R.id.naver_button).setOnClickListener(v -> {
            selectedSocialNetwork = SocialNetwork.NAVER;
            XLogin.startSocialAuth(this, SocialNetwork.NAVER, BuildConfig.WITH_LOGOUT, startSocialCallback);
        });

        final TextView usernameInput = rootView.findViewById(R.id.username_input);
        final TextView passwordInput = rootView.findViewById(R.id.password_input);

        rootView.findViewById(R.id.login_button).setOnClickListener(v -> {
            ViewUtils.disable(v);

            hideKeyboard();
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            boolean disableLogout = "xsolla".equals(username) && "xsolla".equals(password);

            XLogin.login(username, password, !disableLogout && BuildConfig.WITH_LOGOUT, new XLoginCallback<AuthResponse>() {
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        XLogin.finishSocialAuth(getContext(), selectedSocialNetwork, requestCode, resultCode, data, BuildConfig.WITH_LOGOUT, finishSocialCallback);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private StartSocialCallback startSocialCallback = new StartSocialCallback() {
        @Override
        public void onAuthStarted() {
            // auth successfully started
        }

        @Override
        public void onError(@NonNull String errorMessage) {
            showSnack(errorMessage);
        }
    };

    private FinishSocialCallback finishSocialCallback = new FinishSocialCallback() {
        @Override
        public void onAuthSuccess() {
            openFragment(new MainFragment());
        }

        @Override
        public void onAuthCancelled() {
            showSnack("Auth cancelled");
        }

        @Override
        public void onAuthError(@NonNull String errorMessage) {
            showSnack(errorMessage);
        }
    };

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (selectedSocialNetwork != null) {
            outState.putString("selectedSocialNetwork", selectedSocialNetwork.name());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            String selectedSocialNetworkString = savedInstanceState.getString("selectedSocialNetwork");
            if (selectedSocialNetworkString != null) {
                selectedSocialNetwork = SocialNetwork.valueOf(selectedSocialNetworkString);
            }
        }
    }
}

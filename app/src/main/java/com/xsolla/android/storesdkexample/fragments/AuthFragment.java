package com.xsolla.android.storesdkexample.fragments;

import android.view.View;
import android.widget.TextView;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.login.api.XStoreCallback;
import com.xsolla.android.login.social.SocialNetwork;
import com.xsolla.android.login.entity.response.AuthResponse;
import com.xsolla.android.login.entity.response.SocialAuthResponse;
import com.xsolla.android.storesdkexample.R;

public class AuthFragment extends BaseFragment {


    @Override
    int getLayout() {
        return R.layout.fragment_auth;
    }

    @Override
    void initUI() {

        rootView.findViewById(R.id.google_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.loginSocial(SocialNetwork.GOOGLE, socialAuthCallback);
            }
        });

        rootView.findViewById(R.id.facebook_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.loginSocial(SocialNetwork.FACEBOOK, socialAuthCallback);
            }
        });

        rootView.findViewById(R.id.twitter_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.loginSocial(SocialNetwork.TWITTER, socialAuthCallback);
            }
        });

        rootView.findViewById(R.id.linkedin_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.loginSocial(SocialNetwork.LINKEDIN, socialAuthCallback);
            }
        });

        rootView.findViewById(R.id.naver_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.loginSocial(SocialNetwork.NAVER, socialAuthCallback);
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


                XLogin.login(username, password, new XStoreCallback<AuthResponse>() {
                    @Override
                    protected void onSuccess(AuthResponse response) {
                        openFragment(new ProfileFragment());
                    }

                    @Override
                    protected void onFailure(String errorMessage) {
                        showSnack(errorMessage);
                    }
                });


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

    private XStoreCallback<SocialAuthResponse> socialAuthCallback = new XStoreCallback<SocialAuthResponse>() {
        @Override
        protected void onSuccess(SocialAuthResponse response) {
            openFragment(new ProfileFragment());
        }

        @Override
        protected void onFailure(String errorMessage) {
            showSnack(errorMessage);
        }
    };

}

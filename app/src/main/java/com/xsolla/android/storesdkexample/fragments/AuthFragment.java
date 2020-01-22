package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.entity.request.LoginUser;
import com.xsolla.android.xsolla_login_sdk.entity.request.Social;
import com.xsolla.android.xsolla_login_sdk.listener.XAuthListener;
import com.xsolla.android.xsolla_login_sdk.listener.XSocialAuthListener;

public class AuthFragment extends Fragment implements XAuthListener, XSocialAuthListener {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_auth, container, false);
        initUI(rootView);

        return rootView;
    }

    private void initUI(final View rootView) {
        rootView.findViewById(R.id.register_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new RegisterFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        rootView.findViewById(R.id.forgot_password_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .add(R.id.fragment_container, new ResetPasswordFragment())
                        .addToBackStack(null)
                        .commit();
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
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new ProfileFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        showSnack(errorMessage);
    }

    @Override
    public void onSocialLoginSuccess(String token) {
        getFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, new ProfileFragment())
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onSocialLoginFailed(String errorMessage) {
        showSnack(errorMessage);
    }

    private void showSnack(String message) {
        View rootView = getActivity().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }
}

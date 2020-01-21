package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.entity.request.LoginUser;
import com.xsolla.android.xsolla_login_sdk.listener.XAuthListener;

public class AuthFragment extends Fragment implements XAuthListener {


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

        rootView.findViewById(R.id.auth_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = ((TextView) rootView.findViewById(R.id.username_input)).getText().toString();
                String password = ((TextView) rootView.findViewById(R.id.password_input)).getText().toString();
                LoginUser user = new LoginUser(username, password);
                XLogin.getInstance().login(user, AuthFragment.this);
            }
        });
    }


    @Override
    public void onLoginSuccess(String token) {
        Toast.makeText(getContext(), token, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginFailed(String errorMessage) {

    }
}

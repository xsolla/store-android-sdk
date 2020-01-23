package com.xsolla.android.storesdkexample.fragments;

import android.view.View;
import android.widget.TextView;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;

public class ProfileFragment extends BaseFragment {

    @Override
    int getLayout() {
        return R.layout.fragment_profile;
    }

    @Override
    void initUI() {
        String username = XLogin.getInstance().getValue("username");
        String email = XLogin.getInstance().getValue("email");

        TextView usernameLabel = rootView.findViewById(R.id.username);
        TextView emailLabel = rootView.findViewById(R.id.email);

        usernameLabel.setText(username);
        emailLabel.setText(email);

        rootView.findViewById(R.id.logout_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.getInstance().logout();
                openRootFragment();
            }
        });
    }

}

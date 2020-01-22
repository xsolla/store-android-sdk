package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
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
                getFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new AuthFragment())
                        .commit();
            }
        });

        return rootView;
    }


}

package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.listener.XResetPasswordListener;

public class ResetPasswordFragment extends Fragment implements XResetPasswordListener {

    private TextView usernameInput;
    private Button resetPasswordButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_reset_password, container, false);

        usernameInput = rootView.findViewById(R.id.username_input);
        resetPasswordButton = rootView.findViewById(R.id.reset_password_button);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });

        return rootView;
    }

    private void resetPassword() {
        String username = usernameInput.getText().toString();
        XLogin.getInstance().resetPassword(username, this);
    }

    @Override
    public void onResetPasswordSuccess() {
        showSnack("Password reset success. Check your email");

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AuthFragment())
                .commit();
    }

    @Override
    public void onResetPasswordError(String errorMessage) {
        showSnack(errorMessage);
    }

    private void showSnack(String message) {
        View rootView = getActivity().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }
}

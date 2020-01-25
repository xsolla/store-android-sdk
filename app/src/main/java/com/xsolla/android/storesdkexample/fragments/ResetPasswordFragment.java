package com.xsolla.android.storesdkexample.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.listener.XResetPasswordListener;

public class ResetPasswordFragment extends BaseFragment implements XResetPasswordListener {

    private TextView usernameInput;
    private Button resetPasswordButton;

    @Override
    int getLayout() {
        return R.layout.fragment_reset_password;
    }

    @Override
    void initUI() {
        usernameInput = rootView.findViewById(R.id.username_input);
        resetPasswordButton = rootView.findViewById(R.id.reset_password_button);
        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        hideKeyboard();
        String username = usernameInput.getText().toString();
        XLogin.getInstance().resetPassword(username, this);
    }

    @Override
    public void onResetPasswordSuccess() {
        showSnack("Password reset success. Check your email");
        openFragment(new AuthFragment());
    }

    @Override
    public void onResetPasswordError(String errorMessage) {
        showSnack(errorMessage);
    }

}

package com.xsolla.android.storesdkexample.fragments;

import android.widget.Button;
import android.widget.TextView;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.login.api.XLoginCallback;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;

public class ResetPasswordFragment extends BaseFragment {

    private TextView usernameInput;
    private Button resetPasswordButton;

    @Override
    public int getLayout() {
        return R.layout.fragment_reset_password;
    }

    @Override
    public void initUI() {
        usernameInput = rootView.findViewById(R.id.username_input);
        resetPasswordButton = rootView.findViewById(R.id.reset_password_button);
        resetPasswordButton.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        hideKeyboard();
        String username = usernameInput.getText().toString();
        XLogin.resetPassword(username, new XLoginCallback<Void>() {
            @Override
            protected void onSuccess(Void response) {
                showSnack("Password reset success. Check your email");
            }

            @Override
            protected void onFailure(String errorMessage) {
                showSnack(errorMessage);
            }
        });
    }

}

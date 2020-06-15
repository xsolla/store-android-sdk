package com.xsolla.android.storesdkexample.fragments;

import android.widget.Button;
import android.widget.TextView;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.data.auth.Auth;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;
import com.xsolla.android.storesdkexample.util.ViewUtils;

import org.jetbrains.annotations.NotNull;

public class ResetPasswordFragment extends BaseFragment {

    private TextView emailInput;
    private Button resetPasswordButton;

    @Override
    public int getLayout() {
        return R.layout.fragment_reset_password;
    }

    @Override
    public void initUI() {
        emailInput = rootView.findViewById(R.id.username_input);
        resetPasswordButton = rootView.findViewById(R.id.reset_password_button);
        resetPasswordButton.setOnClickListener(v -> {
            ViewUtils.disable(resetPasswordButton);
            resetPassword();
        });
    }

    private void resetPassword() {
        hideKeyboard();
        String email = emailInput.getText().toString();
        Auth.resetPassword(email, new Auth.AuthCallback() {
            @Override
            public void onSuccess() {
                showSnack("Password reset success. Check your email");
                openRootFragment();
                ViewUtils.enable(resetPasswordButton);
            }

            @Override
            public void onFailure(@NotNull String errorMessage) {
                showSnack(errorMessage);
                ViewUtils.enable(resetPasswordButton);
            }
        });
    }

}

package com.xsolla.android.storesdkexample.fragments;

import android.view.View;
import android.widget.TextView;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.data.auth.Auth;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;
import com.xsolla.android.storesdkexample.util.ViewUtils;

import org.jetbrains.annotations.NotNull;

public class AuthFragment extends BaseFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_auth;
    }

    @Override
    public void initUI() {

        rootView.findViewById(R.id.group_social).setVisibility(View.GONE);

        final TextView usernameInput = rootView.findViewById(R.id.username_input);
        final TextView passwordInput = rootView.findViewById(R.id.password_input);

        rootView.findViewById(R.id.login_button).setOnClickListener(v -> {
            ViewUtils.disable(v);

            hideKeyboard();
            String username = usernameInput.getText().toString();
            String password = passwordInput.getText().toString();

            Auth.login(username, password, new Auth.AuthCallback() {
                @Override
                public void onSuccess() {
                    openFragment(new MainFragment());
                    ViewUtils.enable(v);
                }

                @Override
                public void onFailure(@NotNull String errorMessage) {
                    showSnack(errorMessage);
                    ViewUtils.enable(v);
                }
            });
        });

        rootView.findViewById(R.id.forgot_password_button).setOnClickListener(v -> openFragment(new ResetPasswordFragment()));

        rootView.findViewById(R.id.register_button).setOnClickListener(v -> openFragment(new RegisterFragment()));
    }

}

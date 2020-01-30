package com.xsolla.android.storesdkexample.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.login.api.XStoreCallback;
import com.xsolla.android.storesdkexample.R;

public class RegisterFragment extends BaseFragment {

    private TextView usernameInput;
    private TextView emailInput;
    private TextView passwordInput;
    private Button registerButton;

    @Override
    int getLayout() {
        return R.layout.fragment_register;
    }

    @Override
    void initUI() {
        usernameInput = rootView.findViewById(R.id.username_input);
        emailInput = rootView.findViewById(R.id.email_input);
        passwordInput = rootView.findViewById(R.id.password_input);
        registerButton = rootView.findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        hideKeyboard();
        String username = usernameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        XLogin.registerUser(username, email, password, new XStoreCallback<Void>() {
            @Override
            protected void onSuccess(Void response) {
                showSnack("Registration success. Please check your email");
                openFragment(new AuthFragment());
            }

            @Override
            protected void onFailure(String errorMessage) {
                showSnack(errorMessage);
            }
        });
    }

}

package com.xsolla.android.storesdkexample.fragments;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.login.XLogin;
import com.xsolla.android.login.listener.XRegisterListener;

public class RegisterFragment extends BaseFragment implements XRegisterListener {

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

        XLogin.getInstance().registerUser(username, email, password, this);
    }


    @Override
    public void onRegisterSuccess() {
        showSnack("Registration success. Please check your email");
        openFragment(new AuthFragment());
    }

    @Override
    public void onRegisterFailed(String errorMessage) {
        showSnack(errorMessage);
    }

}

package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.entity.request.NewUser;
import com.xsolla.android.xsolla_login_sdk.listener.XRegisterListener;

public class RegisterFragment extends Fragment implements XRegisterListener {

    private TextView usernameInput;
    private TextView emailInput;
    private TextView passwordInput;
    private Button registerButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

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

        return rootView;
    }

    private void registerUser() {
        String username = usernameInput.getText().toString();
        String email = emailInput.getText().toString();
        String password = passwordInput.getText().toString();

        NewUser newUser = new NewUser(username, email, password);
        XLogin.getInstance().registerUser(newUser, this);
    }


    @Override
    public void onRegisterSuccess() {
        showSnack("Registration success. Please check your email");

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new AuthFragment())
                .commit();
    }

    @Override
    public void onRegisterFailed(String errorMessage) {
        showSnack(errorMessage);
    }

    private void showSnack(String message) {
        View rootView = getActivity().findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }
}

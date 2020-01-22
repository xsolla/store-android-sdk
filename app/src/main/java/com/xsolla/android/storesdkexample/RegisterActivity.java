package com.xsolla.android.storesdkexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.entity.request.NewUser;
import com.xsolla.android.xsolla_login_sdk.listener.XRegisterListener;

public class RegisterActivity extends AppCompatActivity implements XRegisterListener {

    TextView usernameInput;
    TextView emailInput;
    TextView passwordInput;
    Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_register);

        XLogin.getInstance().init("753ec123-3245-11ea-b687-42010aa80004", this);
        initUI();
    }

    private void initUI() {
        usernameInput = findViewById(R.id.username_input);
        emailInput = findViewById(R.id.email_input);
        passwordInput = findViewById(R.id.password_input);
        registerButton = findViewById(R.id.register_button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
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
    }

    @Override
    public void onRegisterFailed(String errorMessage) {
        showSnack(errorMessage);
    }

    private void showSnack(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

}

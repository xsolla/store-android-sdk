package com.xsolla.android.storesdkexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.entity.request.LoginUser;

public class LoginActivity extends AppCompatActivity implements XLogin.LoginListener {

    TextView usernameInput;
    TextView passwordInput;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        XLogin.getInstance().init("753ec123-3245-11ea-b687-42010aa80004", this);
        initUI();
    }

    private void initUI() {
        usernameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        loginButton = findViewById(R.id.login_button);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        String username = usernameInput.getText().toString();
        String password = passwordInput.getText().toString();

        LoginUser loginUser = new LoginUser(username, password);
        XLogin.getInstance().login(loginUser, this);
    }

    @Override
    public void onLoginSuccess(String token) {
        showSnack(token);
    }

    @Override
    public void onLoginFailed(String errorMessage) {
        showSnack(errorMessage);
    }

    private void showSnack(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

}

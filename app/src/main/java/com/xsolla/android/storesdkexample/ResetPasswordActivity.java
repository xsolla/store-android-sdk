package com.xsolla.android.storesdkexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.listener.XResetPasswordListener;

public class ResetPasswordActivity extends AppCompatActivity implements XResetPasswordListener {

    TextView usernameInput;
    Button resetPasswordButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        XLogin.getInstance().init("753ec123-3245-11ea-b687-42010aa80004");
        initUI();
    }

    private void initUI() {
        usernameInput = findViewById(R.id.username_input);
        resetPasswordButton = findViewById(R.id.reset_password_button);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        String username = usernameInput.getText().toString();

        XLogin.getInstance().resetPassword(username, this);
    }

    @Override
    public void onResetPasswordSuccess() {
        showSnack("Password reset success. Check your email");
    }

    @Override
    public void onResetPasswordError(String errorMessage) {
        showSnack(errorMessage);
    }

    private void showSnack(String message) {
        View rootView = findViewById(android.R.id.content);
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }
}

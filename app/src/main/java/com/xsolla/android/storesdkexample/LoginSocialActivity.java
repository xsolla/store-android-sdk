package com.xsolla.android.storesdkexample;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.xsolla.android.xsolla_login_sdk.XLogin;
import com.xsolla.android.xsolla_login_sdk.entity.request.Social;

public class LoginSocialActivity extends AppCompatActivity {

    Button facebookButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_social);

        XLogin.getInstance().init("753ec123-3245-11ea-b687-42010aa80004");
        initUI();
    }

    private void initUI() {
        facebookButton = findViewById(R.id.facebook_button);
        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XLogin.getInstance().loginSocial(Social.FACEBOOK, LoginSocialActivity.this);
            }
        });
    }
}

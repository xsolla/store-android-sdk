package com.xsolla.android.storesdkexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.xsolla.android.storesdkexample.fragments.AuthFragment;
import com.xsolla.android.storesdkexample.fragments.ProfileFragment;
import com.xsolla.android.xsolla_login_sdk.XLogin;

public class MainActivity extends AppCompatActivity {

    private final String LOGIN_PROJECT_ID = "753ec123-3245-11ea-b687-42010aa80004";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XLogin.getInstance().init(LOGIN_PROJECT_ID, this);
        initFragment();
    }

    private void initFragment() {
        Fragment fragment = getFragment();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    private Fragment getFragment() {
        String token = XLogin.getInstance().getToken();
        if (token != null && XLogin.getInstance().isTokenValid()) {
            return new ProfileFragment();
        } else {
            return new AuthFragment();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }
}

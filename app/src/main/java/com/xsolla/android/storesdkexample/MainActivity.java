package com.xsolla.android.storesdkexample;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.storesdkexample.fragments.MainFragment;
import com.xsolla.android.storesdkexample.fragments.OldAuthFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XLogin.init(BuildConfig.LOGIN_ID, this, null);
        initFragment();
        initStatusBar();
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
        String token = XLogin.getToken();
        if (token != null && !XLogin.isTokenExpired()) {
            return new MainFragment();
        } else {
            return new OldAuthFragment();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getSupportFragmentManager().getBackStackEntryCount();
        if (count == 1) {
            finish();
        } else {
            getSupportFragmentManager().popBackStack();
        }
    }

    private void initStatusBar() {
        if (Build.VERSION.SDK_INT < 21) {
            return;
        }

        if (Build.VERSION.SDK_INT < 23) {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusBarColorLowApi));
        } else {
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.statusBarColor));
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}

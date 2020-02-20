package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.store.XStore;
import com.xsolla.android.storesdkexample.R;

public class MainFragment extends BaseFragment {

    @Override
    int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = XLogin.getToken();
        XStore.init(44056, token);
    }

    @Override
    void initUI() {
        rootView.findViewById(R.id.virtual_items_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new VirtualItemsFragment());
            }
        });

        rootView.findViewById(R.id.virtual_currency_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new VirtualCurrencyFragment());
            }
        });

        rootView.findViewById(R.id.merchandise_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new PhysicalItemsFragment());
            }
        });

        rootView.findViewById(R.id.profile_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new ProfileFragment());
            }
        });
    }
}

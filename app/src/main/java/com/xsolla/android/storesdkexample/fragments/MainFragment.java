package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.xsolla.android.login.XLogin;
import com.xsolla.android.store.XStore;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;

public class MainFragment extends BaseFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String token = XLogin.getToken();
        XStore.init(48017, token);
    }

    @Override
    public void initUI() {
        rootView.findViewById(R.id.virtual_items_button).setOnClickListener(v -> openFragment(new VirtualItemsFragment()));

        rootView.findViewById(R.id.virtual_currency_button).setOnClickListener(v -> openFragment(new VirtualCurrencyFragment()));

        rootView.findViewById(R.id.merchandise_button).setOnClickListener(v -> openFragment(new PhysicalItemsFragment()));

        rootView.findViewById(R.id.inventory_button).setOnClickListener(v -> openFragment(new InventoryFragment()));

        rootView.findViewById(R.id.profile_button).setOnClickListener(v -> openFragment(new ProfileFragment()));
    }
}

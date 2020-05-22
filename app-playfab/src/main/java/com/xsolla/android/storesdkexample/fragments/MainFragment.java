package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xsolla.android.storesdkexample.BuildConfig;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.data.auth.Auth;
import com.xsolla.android.storesdkexample.data.store.Store;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MainFragment extends BaseFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Store.INSTANCE.init(BuildConfig.PLAYFAB_ID, Auth.INSTANCE.getToken());
    }

    @Override
    public void initUI() {
        getBalance();

        rootView.findViewById(R.id.virtual_items_button).setOnClickListener(v -> openFragment(new VirtualItemsFragment()));
        rootView.findViewById(R.id.virtual_currency_button).setOnClickListener(v -> openFragment(new VirtualCurrencyFragment()));
        rootView.findViewById(R.id.inventory_button).setOnClickListener(v -> openFragment(new InventoryFragment()));

        rootView.findViewById(R.id.profile_button).setOnClickListener(v -> openFragment(new ProfileFragment()));
    }

    private void getBalance() {
        Store.INSTANCE.getVirtualBalance(new Store.VirtualBalanceCallback() {
            @Override
            public void onSuccess(@NonNull List<Store.VirtualBalance> currencies) {
                updateBalanceContainer(currencies);
            }

            @Override
            public void onFailure(@NonNull String errorMessage) {

            }
        });
    }

    private void updateBalanceContainer(List<Store.VirtualBalance> items) {
        LinearLayout balanceContainer = rootView.findViewById(R.id.balance_container);

        for (Store.VirtualBalance item : items) {
            ImageView balanceIcon = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(60, 60);
            layoutParams.setMargins(100, 0, 20, 0);
            balanceIcon.setLayoutParams(layoutParams);
            Glide.with(getContext()).load(item.getImageUrl()).into(balanceIcon);

            balanceContainer.addView(balanceIcon);

            TextView balanceAmount = new TextView(getContext());
            balanceAmount.setText(String.valueOf(item.getAmount()));
            balanceAmount.setTextSize(16f);
            layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(20, 0, 20, 0);

            balanceContainer.addView(balanceAmount);
        }
    }
}

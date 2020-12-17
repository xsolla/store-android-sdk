package com.xsolla.android.storesdkexample.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.xsolla.android.serverlessexample.R;
import com.xsolla.android.storesdkexample.data.store.Store;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;

import java.util.List;

public class MainFragment extends BaseFragment {

    private BroadcastReceiver vcUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getBalance();
        }
    };

    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        getBalance();
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(vcUpdateReceiver, new IntentFilter(Store.ACTION_VC_UPDATE));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(vcUpdateReceiver);
    }

    @Override
    public void initUI() {
        rootView.findViewById(R.id.virtual_items_button).setOnClickListener(v -> openFragment(new VirtualItemsFragment()));
        rootView.findViewById(R.id.virtual_currency_button).setOnClickListener(v -> openFragment(new VirtualCurrencyFragment()));
        rootView.findViewById(R.id.inventory_button).setOnClickListener(v -> openFragment(new InventoryFragment()));
    }

    private void getBalance() {
        Store.getVirtualBalance(new Store.VirtualBalanceCallback() {
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
        balanceContainer.removeAllViews();

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

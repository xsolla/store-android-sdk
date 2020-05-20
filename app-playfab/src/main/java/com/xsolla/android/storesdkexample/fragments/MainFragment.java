package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.inventory.VirtualBalanceResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;

import java.util.List;

import androidx.annotation.Nullable;

public class MainFragment extends BaseFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        String token = XLogin.getToken();
//        XStore.init(BuildConfig.PROJECT_ID, token);
    }

    @Override
    public void initUI() {
//        getBalance();

        rootView.findViewById(R.id.virtual_items_button).setOnClickListener(v -> openFragment(new VirtualItemsFragment()));
        rootView.findViewById(R.id.virtual_currency_button).setOnClickListener(v -> openFragment(new VirtualCurrencyFragment()));
        rootView.findViewById(R.id.merchandise_button).setOnClickListener(v -> openFragment(new PhysicalItemsFragment()));
        rootView.findViewById(R.id.inventory_button).setOnClickListener(v -> openFragment(new InventoryFragment()));

        rootView.findViewById(R.id.profile_button).setOnClickListener(v -> openFragment(new ProfileFragment()));
    }

    private void getBalance() {

        XStore.getVirtualBalance(new XStoreCallback<VirtualBalanceResponse>() {
            @Override
            protected void onSuccess(VirtualBalanceResponse response) {
                updateBalanceContainer(response.getItems());
            }

            @Override
            protected void onFailure(String errorMessage) {

            }
        });
    }

    private void updateBalanceContainer(List<VirtualBalanceResponse.Item> items) {
        LinearLayout balanceContainer = rootView.findViewById(R.id.balance_container);

        for (VirtualBalanceResponse.Item item : items) {
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

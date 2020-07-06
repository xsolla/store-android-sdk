package com.xsolla.android.storesdkexample.fragments;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsolla.android.paystation.XPaystation;
import com.xsolla.android.simplifiedexample.R;
import com.xsolla.android.storesdkexample.adapter.VirtualCurrencyAdapter;
import com.xsolla.android.storesdkexample.data.store.Store;
import com.xsolla.android.storesdkexample.fragments.base.CatalogFragment;
import com.xsolla.android.storesdkexample.listener.CreatePaystationIntentListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VirtualCurrencyFragment extends CatalogFragment {

    private static final int RC_PAYSTATION = 1;

    private VirtualCurrencyAdapter shopAdapter;
    private RecyclerView recyclerView;

    @Override
    public int getLayout() {
        return R.layout.fragment_shop;
    }

    @Override
    public void initUI() {
        recyclerView = rootView.findViewById(R.id.items_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        setupToolbar("Virtual Currency");
        getItems();
    }


    private void getItems() {
        Store.getVirtualCurrencyPacks(new Store.VirtualCurrencyPacksCallback() {
            @Override
            public void onSuccess(@NotNull List<Store.VirtualCurrencyPack> virtualCurrencyPacks) {
                shopAdapter = new VirtualCurrencyAdapter(
                        getContext(),
                        virtualCurrencyPacks,
                        new CreatePaystationIntentListener() {
                            @Override
                            public void onIntentCreated(Intent intent) {
                                startActivityForResult(intent, RC_PAYSTATION);
                            }

                            @Override
                            public void onFailure(String message) {
                                showSnack(message);
                            }
                        });
                recyclerView.setAdapter(shopAdapter);
            }

            @Override
            public void onFailure(@NotNull String errorMessage) {
                showSnack(errorMessage);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PAYSTATION) {
            XPaystation.Result result = XPaystation.Result.fromResultIntent(data);
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getContext(), "Payment OK\n" + result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Payment Fail\n" + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}

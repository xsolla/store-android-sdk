package com.xsolla.android.storesdkexample.fragments;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.xsolla.android.paystation.XPaystation;
import com.xsolla.android.paystation.data.AccessToken;
import com.xsolla.android.storesdkexample.BuildConfig;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.adapter.VirtualItemsAdapter;
import com.xsolla.android.storesdkexample.data.store.Store;
import com.xsolla.android.storesdkexample.fragments.base.CatalogFragment;
import com.xsolla.android.storesdkexample.listener.BuyForVirtualCurrencyListener;
import com.xsolla.android.storesdkexample.listener.CreateOrderListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class VirtualItemsFragment extends CatalogFragment {

    private static final int RC_PAYSTATION = 1;

    private VirtualItemsAdapter virtualItemsAdapter;
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

        setupToolbar("Virtual Items");
        getItems();
    }


    private void getItems() {
        Store.getVirtualItems(new Store.VirtualItemsCallback() {
            @Override
            public void onSuccess(@NotNull List<Store.VirtualItem> virtualItems) {
                virtualItemsAdapter = new VirtualItemsAdapter(
                        virtualItems,
                        new CreateOrderListener() {
                            @Override
                            public void onOrderCreated(String psToken) {
                                Intent intent = XPaystation.createIntentBuilder(getContext())
                                        .accessToken(new AccessToken(psToken))
                                        .useWebview(true)
                                        .isSandbox(BuildConfig.IS_SANDBOX)
                                        .build();
                                startActivityForResult(intent, RC_PAYSTATION);
                            }

                            @Override
                            public void onFailure(String message) {
                                showSnack(message);
                            }
                        },
                        new BuyForVirtualCurrencyListener() {
                            @Override
                            public void onSuccess() {
                                showSnack("Purchased by Virtual currency");
                            }

                            @Override
                            public void onFailure(String errorMessage) {
                                showSnack(errorMessage);
                            }
                        });
                recyclerView.setAdapter(virtualItemsAdapter);
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

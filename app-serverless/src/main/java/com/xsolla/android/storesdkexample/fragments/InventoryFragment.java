package com.xsolla.android.storesdkexample.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsolla.android.serverlessexample.R;
import com.xsolla.android.storesdkexample.adapter.InventoryAdapter;
import com.xsolla.android.storesdkexample.data.store.Store;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;
import com.xsolla.android.storesdkexample.listener.ConsumeListener;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class InventoryFragment extends BaseFragment implements ConsumeListener {

    private BroadcastReceiver viUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getItems();
        }
    };

    private InventoryAdapter inventoryAdapter;
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

        setupToolbar();
    }

    @Override
    public void onResume() {
        super.onResume();
        getItems();
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(viUpdateReceiver, new IntentFilter(Store.ACTION_VI_UPDATE));
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(requireContext())
                .unregisterReceiver(viUpdateReceiver);
    }

    private void getItems() {
        Store.getInventory(new Store.InventoryCallback() {
            @Override
            public void onSuccess(@NotNull List<Store.InventoryItem> inventoryItems) {
                inventoryAdapter = new InventoryAdapter(inventoryItems, InventoryFragment.this);
                recyclerView.setAdapter(inventoryAdapter);
            }

            @Override
            public void onFailure(@NotNull String errorMessage) {
                showSnack(errorMessage);
            }
        });
    }

    @Override
    public void onSuccess() {
        showSnack("Item consumed");
    }

    @Override
    public void onFailure(String errorMessage) {
        showSnack(errorMessage);
    }

    private void setupToolbar() {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setTitle("Inventory");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> popFragment());
    }

}

package com.xsolla.android.storesdkexample.fragments;

import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.inventory.InventoryResponse;
import com.xsolla.android.store.entity.response.inventory.SubscriptionsResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.adapter.InventoryAdapter;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;
import com.xsolla.android.storesdkexample.listener.ConsumeListener;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class InventoryFragment extends BaseFragment implements ConsumeListener {

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
        getItems();
    }


    private void getItems() {
        XStore.getInventory(new XStoreCallback<InventoryResponse>() {
            @Override
            protected void onSuccess(InventoryResponse response) {
                List<InventoryResponse.Item> virtualItems = new ArrayList<>();
                for (InventoryResponse.Item item : response.getItems()) {
                    if (item.getType() == InventoryResponse.Item.Type.VIRTUAL_GOOD) {
                        virtualItems.add(item);
                    }
                }

                inventoryAdapter = new InventoryAdapter(virtualItems, InventoryFragment.this);
                recyclerView.setAdapter(inventoryAdapter);

                getSubscriptions();
            }

            @Override
            protected void onFailure(String errorMessage) {
                showSnack(errorMessage);
            }
        });
    }

    private void getSubscriptions() {
        XStore.getSubscriptions(new XStoreCallback<SubscriptionsResponse>() {
            @Override
            protected void onSuccess(SubscriptionsResponse response) {
                List<SubscriptionsResponse.Item> subscriptions = response.getItems();
                inventoryAdapter.setSubscriptions(subscriptions);
            }

            @Override
            protected void onFailure(String errorMessage) {
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

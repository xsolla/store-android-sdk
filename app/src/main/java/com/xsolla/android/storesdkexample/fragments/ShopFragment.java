package com.xsolla.android.storesdkexample.fragments;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.adapter.ShopAdapter;

public class ShopFragment extends BaseFragment {

    ShopAdapter shopAdapter;
    RecyclerView recyclerView;

    @Override
    int getLayout() {
        return R.layout.fragment_shop;
    }

    @Override
    void initUI() {
        recyclerView = rootView.findViewById(R.id.items_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        getItems();
    }


    private void getItems() {
        XStore.getVirtualItems(new XStoreCallback<VirtualItemsResponse>() {
            @Override
            protected void onSuccess(VirtualItemsResponse response) {
                shopAdapter = new ShopAdapter(response.getItems());
                recyclerView.setAdapter(shopAdapter);
            }

            @Override
            protected void onFailure(String errorMessage) {

            }
        });
    }

}

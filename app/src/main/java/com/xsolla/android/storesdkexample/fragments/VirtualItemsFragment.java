package com.xsolla.android.storesdkexample.fragments;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.adapter.VirtualItemsAdapter;
import com.xsolla.android.storesdkexample.fragments.base.CatalogFragment;
import com.xsolla.android.storesdkexample.listener.AddToCartListener;

public class VirtualItemsFragment extends CatalogFragment implements AddToCartListener {

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
        updateBadge();
    }


    private void getItems() {
        XStore.getVirtualItems(new XStoreCallback<VirtualItemsResponse>() {
            @Override
            protected void onSuccess(VirtualItemsResponse response) {
                virtualItemsAdapter = new VirtualItemsAdapter(response.getItems(), VirtualItemsFragment.this);
                recyclerView.setAdapter(virtualItemsAdapter);
            }

            @Override
            protected void onFailure(String errorMessage) {
                showSnack(errorMessage);
            }
        });
    }

    @Override
    public void onSuccess() {
        updateBadge();
    }

    @Override
    public void onFailure(String errorMessage) {
        showSnack(errorMessage);
    }

}

package com.xsolla.android.storesdkexample.fragments;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.store.entity.response.items.PhysicalItemsResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.adapter.PhysicalItemsAdapter;
import com.xsolla.android.storesdkexample.fragments.base.CatalogFragment;
import com.xsolla.android.storesdkexample.listener.AddToCartListener;

public class PhysicalItemsFragment extends CatalogFragment implements AddToCartListener {

    private PhysicalItemsAdapter virtualItemsAdapter;
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

        setupToolbar("Physical Items");
        getItems();
        updateBadge();
    }


    private void getItems() {
        XStore.getPhysicalItems(new XStoreCallback<PhysicalItemsResponse>() {
            @Override
            protected void onSuccess(PhysicalItemsResponse response) {
                virtualItemsAdapter = new PhysicalItemsAdapter(response.getItems(), PhysicalItemsFragment.this);
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

    private void updateBadge() {
        XStore.getCurrentCart(new XStoreCallback<CartResponse>() {
            @Override
            protected void onSuccess(CartResponse response) {
                int itemsCount = 0;
                for (CartResponse.Item item: response.getItems()) {
                    itemsCount += item.getQuantity();
                }

                setupBadge(itemsCount);
            }

            @Override
            protected void onFailure(String errorMessage) {
                showSnack(errorMessage);
            }
        });
    }

    private void setupBadge(int count) {
        MenuItem cartItem = toolbar.getMenu().findItem(R.id.action_cart);
        View actionView = cartItem.getActionView();
        TextView cartItemCount = actionView.findViewById(R.id.cart_badge);

        if (count == 0) {
            cartItemCount.setVisibility(View.GONE);
        } else {
            cartItemCount.setVisibility(View.VISIBLE);
            cartItemCount.setText(String.valueOf(count));
        }

        actionView.setOnClickListener(v -> openFragment(new CartFragment()));
    }


}

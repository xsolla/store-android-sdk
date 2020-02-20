package com.xsolla.android.storesdkexample.fragments;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.adapter.VirtualItemsAdapter;
import com.xsolla.android.storesdkexample.listener.AddToCartListener;

public class VirtualItemsFragment extends BaseFragment implements AddToCartListener {

    private VirtualItemsAdapter virtualItemsAdapter;
    private RecyclerView recyclerView;
    private Toolbar toolbar;

    @Override
    int getLayout() {
        return R.layout.fragment_shop;
    }

    @Override
    void initUI() {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setTitle("Virtual Items");

        recyclerView = rootView.findViewById(R.id.items_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

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

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFragment(new CartFragment());
            }
        });
    }


}

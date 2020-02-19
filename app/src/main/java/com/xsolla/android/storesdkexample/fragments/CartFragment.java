package com.xsolla.android.storesdkexample.fragments;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.adapter.CartAdapter;

public class CartFragment extends BaseFragment {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;

    @Override
    int getLayout() {
        return R.layout.fragment_cart;
    }

    @Override
    void initUI() {
        recyclerView = rootView.findViewById(R.id.items_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        getItems();
    }

    private void getItems() {
        XStore.getCurrentCart(new XStoreCallback<CartResponse>() {
            @Override
            protected void onSuccess(CartResponse response) {
                cartAdapter = new CartAdapter(response.getItems());
                recyclerView.setAdapter(cartAdapter);
            }

            @Override
            protected void onFailure(String errorMessage) {

            }
        });
    }
}

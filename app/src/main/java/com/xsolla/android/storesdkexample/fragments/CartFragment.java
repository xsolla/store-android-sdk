package com.xsolla.android.storesdkexample.fragments;

import android.widget.TextView;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsolla.android.sdk.XsollaSDK;
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.payment.PaymentOptions;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.adapter.CartAdapter;
import com.xsolla.android.storesdkexample.fragments.base.CatalogFragment;
import com.xsolla.android.storesdkexample.listener.UpdateCartListener;

public class CartFragment extends CatalogFragment implements UpdateCartListener {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;

    @Override
    public int getLayout() {
        return R.layout.fragment_cart;
    }

    @Override
    public void initUI() {
        setupToolbar();
        recyclerView = rootView.findViewById(R.id.items_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(dividerItemDecoration);

        TextView checkoutButton = rootView.findViewById(R.id.checkout_button);
        checkoutButton.setOnClickListener(v -> {
            PaymentOptions paymentOptions = new PaymentOptions().create()
                    .setSandbox(true)
                    .build();

            XStore.createOrderFromCurrentCart(paymentOptions, new XStoreCallback<CreateOrderResponse>() {
                @Override
                protected void onSuccess(CreateOrderResponse response) {
                    XsollaSDK.createPaymentForm(getContext(), response.getToken(), true);
                }

                @Override
                protected void onFailure(String errorMessage) {
                    showSnack(errorMessage);
                }
            });
        });

        getItems();
    }

    private void getItems() {
        XStore.getCurrentCart(new XStoreCallback<CartResponse>() {
            @Override
            protected void onSuccess(CartResponse response) {
                cartAdapter = new CartAdapter(response.getItems(), CartFragment.this);
                recyclerView.setAdapter(cartAdapter);
                onCartUpdated(response.getPrice().getPrettyPrintAmount());
            }

            @Override
            protected void onFailure(String errorMessage) {

            }
        });
    }

    private void setupToolbar() {
        toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> popFragment());
    }

    @Override
    public void onCartUpdated(String totalAmount) {
        toolbar.setTitle("Total: " + totalAmount);
    }
}

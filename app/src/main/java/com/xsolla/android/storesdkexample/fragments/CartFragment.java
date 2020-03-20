package com.xsolla.android.storesdkexample.fragments;

import android.app.Activity;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xsolla.android.paystation.XPaystation;
import com.xsolla.android.paystation.data.AccessToken;
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.payment.PaymentOptions;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse;
import com.xsolla.android.storesdkexample.BuildConfig;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.adapter.CartAdapter;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;
import com.xsolla.android.storesdkexample.listener.UpdateCartListener;

public class CartFragment extends BaseFragment implements UpdateCartListener {

    private static final int RC_PAYSTATION = 1;

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Toolbar toolbar;

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
                    .setSandbox(BuildConfig.IS_SANDBOX)
                    .build();

            XStore.createOrderFromCurrentCart(paymentOptions, new XStoreCallback<CreateOrderResponse>() {
                @Override
                protected void onSuccess(CreateOrderResponse response) {
                    Intent intent = XPaystation.createIntentBuilder(getContext())
                            .accessToken(new AccessToken(response.getToken()))
                            .isSandbox(BuildConfig.IS_SANDBOX)
                            .build();
                    startActivityForResult(intent, RC_PAYSTATION);
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
                if (!response.getItems().isEmpty()) {
                    cartAdapter = new CartAdapter(response.getItems(), CartFragment.this);
                    recyclerView.setAdapter(cartAdapter);
                    onCartUpdated(response.getPrice().getPrettyPrintAmount());
                }
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

    @Override
    public void onCartEmpty() {
        openFragment(new MainFragment());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_PAYSTATION) {
            XPaystation.Result result = XPaystation.Result.fromResultIntent(data);
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getContext(), "OK\n" + result, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getContext(), "Fail\n" + result, Toast.LENGTH_LONG).show();
            }
        }
    }
}

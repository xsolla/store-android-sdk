package com.xsolla.android.storesdkexample.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.xsolla.android.paystation.XPaystation;
import com.xsolla.android.paystation.data.AccessToken;
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.payment.PaymentOptions;
import com.xsolla.android.store.entity.response.cart.CartResponse;
import com.xsolla.android.store.entity.response.order.OrderResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse;
import com.xsolla.android.storesdkexample.BuildConfig;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.adapter.CartAdapter;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;
import com.xsolla.android.storesdkexample.listener.UpdateCartListener;
import com.xsolla.android.storesdkexample.util.ViewUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class CartFragment extends BaseFragment implements UpdateCartListener {

    private static final int RC_PAYSTATION = 1;

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private Toolbar toolbar;

    private String orderId;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            orderId = savedInstanceState.getString("orderId");
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("orderId", orderId);
    }

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
            ViewUtils.disable(v);
            PaymentOptions paymentOptions = new PaymentOptions().create()
                    .setSandbox(BuildConfig.IS_SANDBOX)
                    .build();

            XStore.createOrderFromCurrentCart(paymentOptions, new XStoreCallback<CreateOrderResponse>() {
                @Override
                protected void onSuccess(CreateOrderResponse response) {
                    orderId = Integer.toString(response.getOrderId());
                    Intent intent = XPaystation.createIntentBuilder(getContext())
                            .accessToken(new AccessToken(response.getToken()))
                            .isSandbox(BuildConfig.IS_SANDBOX)
                            .build();
                    startActivityForResult(intent, RC_PAYSTATION);
                    ViewUtils.enable(v);
                }

                @Override
                protected void onFailure(String errorMessage) {
                    showSnack(errorMessage);
                    ViewUtils.enable(v);
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
                showSnack("Payment is completed");
                XStore.getOrder(orderId, new XStoreCallback<OrderResponse>() {
                    @Override
                    protected void onSuccess(OrderResponse response) {
                        if (response.getStatus() == OrderResponse.Status.DONE) {
                            showSnack("Order is done");
                            openFragment(new MainFragment());
                            XStore.clearCurrentCart(new XStoreCallback<Void>() {
                                @Override
                                protected void onSuccess(Void response) {

                                }

                                @Override
                                protected void onFailure(String errorMessage) {
                                    showSnack(errorMessage);
                                }
                            });
                        }
                    }

                    @Override
                    protected void onFailure(String errorMessage) {
                        showSnack(errorMessage);
                    }
                });
            } else {
                showSnack("Payment is canceled");
            }
        }
    }
}
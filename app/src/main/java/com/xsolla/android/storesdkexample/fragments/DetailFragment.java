package com.xsolla.android.storesdkexample.fragments;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.xsolla.android.sdk.XsollaSDK;
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.payment.PaymentOptions;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;
import com.xsolla.android.storesdkexample.util.ViewUtils;

public class DetailFragment extends BaseFragment {

    private TextView checkoutButton;

    private VirtualItemsResponse.Item item;

    @Override
    public int getLayout() {
        return R.layout.fragment_detail;
    }

    public static DetailFragment newInstance(VirtualItemsResponse.Item item) {
        Bundle args = new Bundle();
        args.putParcelable("item", item);
        DetailFragment fragment = new DetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments().getParcelable("item");
    }

    @Override
    public void initUI() {
        initToolbar();
        ImageView itemIcon = rootView.findViewById(R.id.item_icon);
        Glide.with(getContext()).load(item.getImageUrl()).into(itemIcon);
        TextView itemName = rootView.findViewById(R.id.item_name);
        itemName.setText(item.getName());

        checkoutButton = rootView.findViewById(R.id.checkout_button);
        initCheckoutButton();
    }


    private void initToolbar() {
        Toolbar toolbar = rootView.findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(v -> popFragment());
    }


    private void initCheckoutButton() {
        String buttonText = "Buy for " + item.getPrice().getPrettyPrintAmount();
        checkoutButton.setText(buttonText);
        checkoutButton.setOnClickListener(v -> {
            ViewUtils.disable(v);
            PaymentOptions options = new PaymentOptions().create()
                    .setSandbox(true)
                    .build();

            XStore.createOrderByItemSku(item.getSku(), options, new XStoreCallback<CreateOrderResponse>() {
                @Override
                protected void onSuccess(CreateOrderResponse response) {
                    String token = response.getToken();
                    XsollaSDK.createPaymentForm(getContext(), token, true);
                    ViewUtils.enable(v);
                }

                @Override
                protected void onFailure(String errorMessage) {
                    showSnack(errorMessage);
                    ViewUtils.enable(v);
                }
            });
        });
    }

}

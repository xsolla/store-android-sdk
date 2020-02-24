package com.xsolla.android.storesdkexample.fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.bumptech.glide.Glide;
import com.xsolla.android.sdk.XsollaSDK;
import com.xsolla.android.store.XStore;
import com.xsolla.android.store.api.XStoreCallback;
import com.xsolla.android.store.entity.request.payment.PaymentOptions;
import com.xsolla.android.store.entity.response.common.VirtualPrice;
import com.xsolla.android.store.entity.response.items.VirtualItemsResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderByVirtualCurrencyResponse;
import com.xsolla.android.store.entity.response.payment.CreateOrderResponse;
import com.xsolla.android.storesdkexample.R;
import com.xsolla.android.storesdkexample.fragments.base.BaseFragment;

import java.util.List;

public class DetailFragment extends BaseFragment {

    private RadioGroup radioGroup;
    private TextView checkoutButton;

    private VirtualItemsResponse.Item item;

    private int checkedIndex = -1;

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
    public void initUI() {
        ImageView itemIcon = rootView.findViewById(R.id.item_icon);
        Glide.with(getContext()).load(item.getImageUrl()).into(itemIcon);

        TextView itemName = rootView.findViewById(R.id.item_name);
        itemName.setText(item.getName());

        radioGroup = rootView.findViewById(R.id.radio_group);
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = radioGroup.findViewById(checkedId);
            checkedIndex = radioGroup.indexOfChild(checkedRadioButton);
        });

        checkoutButton = rootView.findViewById(R.id.checkout_button);
        initPaymentMethodSelector();
        initCheckoutButton();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        item = getArguments().getParcelable("item");
    }

    private void initPaymentMethodSelector() {
        Typeface font = ResourcesCompat.getFont(getContext(), R.font.roboto);

        RadioButton button = new RadioButton(getContext());
        button.setText(item.getPrice().getPrettyPrintAmount());
        button.setTextSize(24f);
        button.setTypeface(font);
        radioGroup.addView(button);

        List<VirtualPrice> virtualPrices = item.getVirtualPrices();
        for (VirtualPrice price : virtualPrices) {
            RadioButton radioButton = new RadioButton(getContext());
            radioButton.setText(price.getAmount() + " " + price.getName());
            radioButton.setTextSize(24f);
            radioButton.setTypeface(font);
            radioGroup.addView(radioButton);
        }
    }

    private void initCheckoutButton() {
        checkoutButton.setOnClickListener(v -> {
            if (checkedIndex == -1) {
                showSnack("Please select payment method");
            }

            if (checkedIndex == 0) {
                PaymentOptions options = new PaymentOptions().create()
                        .setSandbox(true)
                        .build();

                XStore.createOrderByItemSku(item.getSku(), options, new XStoreCallback<CreateOrderResponse>() {
                    @Override
                    protected void onSuccess(CreateOrderResponse response) {
                        String token = response.getToken();
                        XsollaSDK.createPaymentForm(getContext(), token, true);
                    }

                    @Override
                    protected void onFailure(String errorMessage) {
                        showSnack(errorMessage);
                    }
                });

            } else {
                VirtualPrice virtualPrice = item.getVirtualPrices().get(checkedIndex - 1);
                XStore.createOrderByVirtualCurrency(item.getSku(), virtualPrice.getSku(), new XStoreCallback<CreateOrderByVirtualCurrencyResponse>() {
                    @Override
                    protected void onSuccess(CreateOrderByVirtualCurrencyResponse response) {
                        showSnack("Purchased by Virtual currency");
                        openFragment(new MainFragment());
                    }

                    @Override
                    protected void onFailure(String errorMessage) {
                        showSnack(errorMessage);
                    }
                });
            }
        });

    }

}
